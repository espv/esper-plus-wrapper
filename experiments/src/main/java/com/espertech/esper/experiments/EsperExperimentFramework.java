package com.espertech.esper.experiments;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EPException;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.util.EventTypeBusModifier;
import com.espertech.esper.common.client.util.NameAccessModifier;
import com.espertech.esper.common.internal.event.map.MapEventBean;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import no.uio.ifi.ExperimentAPI;
import no.uio.ifi.SpeComm;
import no.uio.ifi.TracingFramework;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class EsperExperimentFramework implements ExperimentAPI {
    private long timeLastRecvdTuple = 0;
    public TracingFramework tf = new TracingFramework();
    private int number_threads = 1;
    List<StreamListener> streamListeners = new ArrayList<>();

    TCPNettyServer tcpNettyServer;
    Map<Integer, TCPNettyClient> nodeIdToClient = new HashMap<>();
    Map<String, Integer> streamNameToId = new HashMap<>();
    Map<Integer, Map<String, Object>> nodeIdToIpAndPort = new HashMap<>();
    Map<Integer, List<Integer>> streamIdToNodeIds = new HashMap<>();
    Map<Integer, List<Map<String, Object>>> datasetIdToTuples = new HashMap<>();
    int port;

    ArrayList<Map<String, Object>> allPackets = new ArrayList<>();
    ArrayList<Integer> eventIDs = new ArrayList<>();

    ArrayList<String> queries = new ArrayList<>();
    Map<Integer, Map<String, Object>> allSchemas = new HashMap<>();
    Map<Integer, String> esper_schemas = new HashMap<>();
    private String trace_output_folder;
    Map<Integer, BufferedWriter> streamIdToCsvWriter = new HashMap<>();

    public void SetTraceOutputFolder(String f) {this.trace_output_folder = f;}

    @Override
    public String SetTupleBatchSize(int size) {
        //batch_size = size;
        return "Success";
    }

    @Override
    public String SetIntervalBetweenTuples(int interval) {
        //interval_wait = interval;
        return "Success";
    }

    @Override
    public String AddTuples(Map<String, Object> tuple, int quantity) {
        List<Map<String, Object>> attributes = (ArrayList<Map<String, Object>>) tuple.get("attributes");
        Map<String, Object> esper_attributes = new HashMap<>();
        int stream_id = (int) tuple.get("stream-id");

        for (Map<String, Object> attribute : attributes) {
            esper_attributes.put((String) attribute.get("name"), attribute.get("value"));
        }
        esper_attributes.put("stream-id", stream_id);

        for (int i = 0; i < quantity; i++) {
            allPackets.add(esper_attributes);
        }
        return "Success";
    }

    private Map<String, Object> GetMapFromYaml(Map<String, Object> ds) {
        FileInputStream fis = null;
        Yaml yaml = new Yaml();
        String dataset_path = System.getenv().get("EXPOSE_PATH") + "/" + ds.get("file");
        try {
            fis = new FileInputStream(dataset_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return (Map<String, Object>) yaml.load(fis);
    }

    @Override
    public String SendDsAsStream(Map<String, Object> ds) {
        //System.out.println("Processing dataset");
        int ds_id = (int) ds.get("id");
        List<Map<String, Object>> tuples = datasetIdToTuples.get(ds_id);
        if (tuples == null) {
            Map<String, Object> map = GetMapFromYaml(ds);
            List<Map<String, Object>> raw_tuples = (List<Map<String, Object>>) map.get("cepevents");
            Map<Integer, List<Map<String, Object>>> ordered_tuples = new HashMap<>();
            //int i = 0;
            // Add order to tuples and place them in ordered_tuples
            for (Map<String, Object> tuple : raw_tuples) {
                //tuple.put("_order", i++);
                int tuple_stream_id = (int) tuple.get("stream-id");
                if (ordered_tuples.get(tuple_stream_id) == null) {
                    ordered_tuples.put(tuple_stream_id, new ArrayList<>());
                }
                ordered_tuples.get(tuple_stream_id).add(tuple);
            }

            // Fix the type of the tuples in ordered_tuples
            for (int stream_id : ordered_tuples.keySet()) {
                Map<String, Object> schema = allSchemas.get(stream_id);
                CastTuplesCorrectTypes(ordered_tuples.get(stream_id), schema);
            }

            // Sort the raw_tuples by their order
			/*raw_tuples.sort((lhs, rhs) -> {
				int lhs_order = (int) lhs.get("_order");
				int rhs_order = (int) rhs.get("_order");
				return Integer.compare(lhs_order, rhs_order);
			});*/

            datasetIdToTuples.put(ds_id, raw_tuples);
            tuples = raw_tuples;
        }
		/*double prevTimestamp = 0;
		//System.out.println("Ready to transmit tuples");
		long prevTime = System.nanoTime();
		boolean realism = (boolean) ds.getOrDefault("realism", false) && schema.containsKey("rowtime-column");
		for (Map<String, Object> tuple : tuples) {
			AddTuples(tuple, 1);

			if (realism) {
				Map<String, Object> rowtime_column = (Map<String, Object>) schema.get("rowtime-column");
				double timestamp = 0;
				for (Map<String, Object> attribute : (List<Map<String, Object>>) tuple.get("attributes")) {
					if (attribute.get("name").equals(rowtime_column.get("column"))) {
						int nanoseconds_per_tick = (int) rowtime_column.get("nanoseconds-per-tick");
						timestamp = (double) attribute.get("value") * nanoseconds_per_tick;
						if (prevTimestamp == 0) {
							prevTimestamp = timestamp;
						}
						break;
					}
				}
				double time_diff_tuple = timestamp - prevTimestamp;
				long time_diff_real = System.nanoTime() - prevTime;
				while (time_diff_real < time_diff_tuple) {
					time_diff_real = System.nanoTime() - prevTime;
				}

				prevTimestamp = timestamp;
				prevTime = System.nanoTime();
			}
		}

		if (!realism) {
			ProcessTuples(tuples.size());
		}*/

        for (Map<String, Object> tuple : tuples) {
            AddTuples(tuple, 1);
        }
        ProcessTuples(tuples.size());
        return "Success";
    }

    @Override
    public String AddDataset(Map<String, Object> ds) {
        int stream_id = (int) ds.get("stream-id");
        Map<String, Object> schema = allSchemas.get(stream_id);

        if (ds.get("type").equals("csv")) {
            List<String> fields = new ArrayList<>();
            List<Class<?>> attr_types = new ArrayList<>();
            List<Map<String, Object>> tuple_format = (ArrayList<Map<String, Object>>) schema.get("tuple-format");
            for (Map<String, Object> stringObjectMap : tuple_format) {
                if (stringObjectMap.get("type").equals("string")) {
                    attr_types.add(String.class);
                } else if (stringObjectMap.get("type").equals("bool")) {
                    attr_types.add(Boolean.class);
                } else if (stringObjectMap.get("type").equals("int")) {
                    attr_types.add(Integer.class);
                } else if (stringObjectMap.get("type").equals("float")) {
                    attr_types.add(Float.class);
                } else if (stringObjectMap.get("type").equals("double")) {
                    attr_types.add(Double.class);
                } else if (stringObjectMap.get("type").equals("long")) {
                    attr_types.add(Long.class);
                } else if (stringObjectMap.get("type").equals("number")) {
                    attr_types.add(Float.class);
                } else if (stringObjectMap.get("type").equals("timestamp")) {
                    attr_types.add(String.class);
                } else if (stringObjectMap.get("type").equals("long-timestamp")) {
                    attr_types.add(Long.class);
                } else {
                    throw new RuntimeException("Invalid attribute type in dataset definition");
                }

                fields.add((String) stringObjectMap.get("name"));
            }

            String dataset_path = System.getenv().get("EXPOSE_PATH") + "/" + ds.get("file");
            try {
                BufferedReader csvReader = new BufferedReader(new FileReader(dataset_path));

                Map<String, Object> esper_attributes = new HashMap<>();
                String row;
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    Object attr;
                    for (int i = 0; i < data.length; i++) {
                        if (attr_types.get(i) == String.class) {
                            attr = data[i];
                        } else if (attr_types.get(i) == Float.class) {
                            attr = Float.parseFloat(data[i]);
                        } else {
                            throw new RuntimeException("Invalid attribute type in dataset definition");
                        }
                        esper_attributes.put(fields.get(i), attr);
                    }
                    esper_attributes.put("stream-id", stream_id);
                    allPackets.add(esper_attributes);
                }
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
        		System.exit(20);
            }
        } else if (ds.get("type").equals("yaml")) {
            List<Map<String, Object>> tuples = readTuplesFromDataset(ds, schema);
            for (Map<String, Object> tuple : tuples) {
                AddTuples(tuple, 1);
            }
        } else {
            throw new RuntimeException("Invalid dataset type for dataset with Id " + ds.get("id"));
        }
        return "Success";
    }

    void CastTuplesCorrectTypes(List<Map<String, Object>> tuples, Map<String, Object> schema) {
        List<Map<String, String>> tuple_format = (ArrayList<Map<String, String>>) schema.get("tuple-format");
        for (Map<String, Object> tuple : tuples) {
            List<Map<String, Object>> attributes = (List<Map<String, Object>>) tuple.get("attributes");
            for (int i = 0; i < tuple_format.size(); i++) {
                Map<String, String> attribute_format = tuple_format.get(i);
                Map<String, Object> attribute = attributes.get(i);
                switch (attribute_format.get("type")) {
                    case "string":
                        attribute.put("value", attribute.get("value").toString());
                        break;
                    case "bool":
                        attribute.put("value", Boolean.valueOf(attribute.get("value").toString()));
                        break;
                    case "int":
                        attribute.put("value", Integer.parseInt(attribute.get("value").toString()));
                        break;
                    case "float":
                        attribute.put("value", Float.parseFloat(attribute.get("value").toString()));
                        break;
                    case "double":
                        attribute.put("value", Double.parseDouble(attribute.get("value").toString()));
                        break;
                    case "long":
                        attribute.put("value", Long.parseLong(attribute.get("value").toString()));
                        break;
                    case "long-timestamp":
                        // Not using rowtime yet, and Timestamp class not supported in schema
                        //attribute.put("value", new Timestamp(Long.parseLong(attribute.get("value").toString())));
                        attribute.put("value", Long.parseLong(attribute.get("value").toString()));
                        break;
                    case "timestamp":
                        // Not using rowtime yet, and Timestamp class not supported in schema
                        //attribute.put("value", Timestamp.valueOf(attribute.get("value").toString()));
                        attribute.put("value", Long.parseLong(attribute.get("value").toString()));
                        break;
                    default:
                        throw new RuntimeException("Invalid attribute type in dataset definition");
                }
            }
        }
    }

    private List<Map<String, Object>> readTuplesFromDataset(Map<String, Object> ds, Map<String, Object> schema) {
        int stream_id = (int) ds.get("stream-id");
        List<Map<String, Object>> tuples = datasetIdToTuples.get(stream_id);
        if (tuples == null) {
            FileInputStream fis = null;
            Yaml yaml = new Yaml();
            String dataset_path = System.getenv().get("EXPOSE_PATH") + "/" + ds.get("file");
            try {
                fis = new FileInputStream(dataset_path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
		        System.exit(21);
            }
            Map<String, Object> map = yaml.load(fis);
            tuples = (ArrayList<Map<String, Object>>) map.get("cepevents");
            CastTuplesCorrectTypes(tuples, schema);
            datasetIdToTuples.put(stream_id, tuples);
        }
        return tuples;
    }

    void ProcessTuple(int stream_id, String stream_name, Map<String, Object> mappedBean) {
        if (streamIdToNodeIds.containsKey(stream_id)) {
            for (int nodeId : streamIdToNodeIds.get(stream_id)) {
                TCPNettyClient tcpNettyClient = nodeIdToClient.get(nodeId);
                if (tcpNettyClient == null) {
                    tcpNettyClient = new TCPNettyClient(true, true);
                    nodeIdToClient.put(nodeId, tcpNettyClient);
                    try {
                        for (int nid : nodeIdToIpAndPort.keySet()) {
                            if (nodeId == nid) {
                                Map<String, Object> addrAndPort = nodeIdToIpAndPort.get(nid);
                                tcpNettyClient.connect((String) addrAndPort.get("ip"), (int) addrAndPort.get("port"));
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(6);
                    }
                }

                try {
                    tcpNettyClient.send(stream_name, mappedBean).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(6);
                }
            }
        }
    }

    int tupleCnt = 0;
    @Override
    public String AddSchemas(List<Map<String, Object>> schemas) {
        for (Map<String, Object> schema : schemas) {
            int stream_id = (int) schema.get("stream-id");
            allSchemas.put(stream_id, schema);
            String stream_name = (String) schema.get("name");
            streamNameToId.put(stream_name, stream_id);


            StringBuilder esper_schema = new StringBuilder("create schema " + stream_name + " (");
            for (Map<String, Object> j : (ArrayList<Map<String, Object>>) schema.get("tuple-format")) {
                esper_schema.append(j.get("name"));
                esper_schema.append(" ");
                if (j.get("type").equals("string")) {
                    esper_schema.append("string, ");
                } else if (j.get("type").equals("bool")) {
                    esper_schema.append("bool, ");
                } else if (j.get("type").equals("int")) {
                    esper_schema.append("int, ");
                } else if (j.get("type").equals("float")) {
                    esper_schema.append("double, ");
                } else if (j.get("type").equals("double")) {
                    esper_schema.append("double, ");
                } else if (j.get("type").equals("long")) {
                    esper_schema.append("long, ");
                } else if (j.get("type").equals("number")) {
                    esper_schema.append("double, ");
                } else if (j.get("type").equals("timestamp")) {
                    esper_schema.append("string, ");
                } else if (j.get("type").equals("long-timestamp")) {
                    esper_schema.append("long, ");
                } else {
                    throw new RuntimeException("Invalid attribute type in stream schema");
                }
            }
            // We remove the final comma and space
            esper_schema.setLength(esper_schema.length()-2);
            esper_schema.append(");\n");
            esper_schemas.put(stream_id, esper_schema.toString());

            StreamListener sl = new StreamListener() {
                @Override
                public String getChannelId() {
                    return stream_name;
                }

                @Override
                public void onMessage(Map<String, Object> message) {
                    // if (++tupleCnt % 10000 == 0) {
                    System.out.println("Received tuple " + (++tupleCnt) + ": " + message);
                    //}
                    onEvent(message);
                }

                public void onEvent(Map<String, Object> mappedBean) {
                    timeLastRecvdTuple = System.currentTimeMillis();
                    BufferedWriter writer = streamIdToCsvWriter.get(stream_id);
                    if (writer != null) {
                        try {
                            writer.write(mappedBean.toString() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        tf.traceEvent(1, new Object[]{Thread.currentThread().getId()});
                        runtime.getEventService().sendEventMap(mappedBean, stream_name);
                        tf.traceEvent(100, new Object[]{Thread.currentThread().getId()});
                    } catch (EPException e) {
                        e.printStackTrace();
                        System.exit(16);
                    }
                }
            };

            this.streamListeners.add(sl);
            this.tcpNettyServer.addStreamListener(sl);
        }
        return "Success";
    }

    Configuration config = new Configuration();
    CompilerArguments args;
    EPRuntime runtime;
    EPEventService eventService;
    EPCompiler compiler;
    boolean isRuntimeActive = false;

    @Override
    public String DeployQueries(Map<String, Object> json_query) {
        String query = (String) ((Map<String, Object>) json_query.get("sql-query")).get("esper");
        if (query == null || query.equals("")) {
            return "Empty query";
        }
        int query_id = (int) json_query.get("id");
        tf.traceEvent(221, new Object[]{query_id});
        queries.add(query);
        return "Success";
    }

    int number_complex_events = 0;
    class ExperimentListener implements UpdateListener {
        public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {
            if (++number_complex_events % 100000 == 0) {
                System.out.println("Produced " + number_complex_events + " complex events");
            }
            //System.out.println("ExperimentListener called with " + newEvents.length + " new events");
            for (EventBean bean : newEvents) {
                String stream_name = bean.getEventType().getMetadata().getName();
                //System.out.println("ExperimentListener.update, here we forward the complex event of stream " + stream_name);
                int stream_id = streamNameToId.get(stream_name);
                ProcessTuple(stream_id, stream_name, ((MapEventBean) bean).getProperties());
                tf.traceEvent(6);
            }
        }
    }

    @Override
    public String ProcessTuples(int number_tuples) {
        //System.out.println("Processing tuples");

        if (allPackets.isEmpty()) {
            String ret = "No tuples to process";
            System.out.println(ret);
            return ret;
        }
        for (int i = 0; i < number_tuples; i++) {
            Map<String, Object> mappedBean = allPackets.get(i % allPackets.size());
            Integer stream_id = (Integer) mappedBean.get("stream-id");
            String stream_name = (String) allSchemas.get(stream_id).get("name");
            ProcessTuple(stream_id, stream_name, mappedBean);
        }

        //pktsPublished = 0;
        return "Success";
    }

    @Override
    public String StartRuntimeEnv() {
        StringBuilder allQueries = new StringBuilder();
        for (String q : queries) {
            allQueries.append(q).append("\n");
        }

        StringBuilder schemas_string = new StringBuilder();
        for (String esper_schema : esper_schemas.values()) {
            schemas_string.append(esper_schema);
        }

        try {
            runtime.getDeploymentService().undeployAll();
        } catch (EPUndeployException e) {
            e.printStackTrace();
	        System.exit(16);
        }
        try {
            String toDeploy = schemas_string.toString() + allQueries.toString();
            EPCompiled compiled = compiler.compile(toDeploy, args);
            ExperimentListener listener = new ExperimentListener();
            EPStatement[] stmts = runtime.getDeploymentService().deploy(compiled).getStatements();
            for (EPStatement stmt : stmts) {
                stmt.addListener(listener);
            }
        } catch (EPCompileException | EPDeployException | EPException e) {
            e.printStackTrace();
	        System.exit(17);
        }
        timeLastRecvdTuple = 0;
        return "Success";
    }

    @Override
    public String StopRuntimeEnv() {
        tf.writeTraceToFile(this.trace_output_folder, this.getClass().getSimpleName());
        return "Success";
    }

    //@Override
    //public void setNodeId(int nodeId) {this.nodeId = nodeId;}

    public void SetupClientTcpServer(int port) {
        this.tcpNettyServer = new TCPNettyServer();
        this.port = port;
        ServerConfig sc = new ServerConfig();
        sc.setPort(port);
        tcpNettyServer.start(sc);
    }

    @Override
    public String SetNidToAddress(Map<Integer, Map<String, Object>> newNodeIdToIpAndPort) {
        nodeIdToIpAndPort = newNodeIdToIpAndPort;
        System.out.println("SetNidToAddress, node Id - IP and port: " + newNodeIdToIpAndPort);
        return "Success";
    }


    @Override
    public String WriteStreamToCsv(int stream_id, String csv_folder) {
        int cnt = 1;
        boolean finished = false;
        while (!finished) {
            String path = csv_folder + "/esper/" + cnt;
            Path p = Paths.get(path);
            if (Files.exists(p)) {
                ++cnt;
                continue;
            }
            File f = new File(path);
            if (!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileWriter fw = null;
            try {
                fw = new FileWriter(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bw = new BufferedWriter(fw);
            streamIdToCsvWriter.put(stream_id, bw);
            finished = true;
        }
        return "Success";
    }


    @Override
    public String AddNextHop(int streamId, int nodeId) {
        if (!streamIdToNodeIds.containsKey(streamId)) {
            streamIdToNodeIds.put(streamId, new ArrayList<>());
        }
        streamIdToNodeIds.get(streamId).add(nodeId);
        return "Success";
    }

    @Override
    public String ClearQueries() {
        tf.traceEvent(222);
        if (runtime == null) {
            return "Runtime hasn't been initialized";
        }
        try {
            runtime.getDeploymentService().undeployAll();
        } catch (EPUndeployException e) {
            e.printStackTrace();
	        System.exit(18);
        }
        return "Success";
    }

    @Override
    public String ClearTuples() {
        allPackets.clear();
        tf.traceEvent(223);
        return "Success";
    }

    @Override
    public String EndExperiment() {
        tf.writeTraceToFile(this.trace_output_folder, this.getClass().getSimpleName());
        for (BufferedWriter w : streamIdToCsvWriter.values()) {
            try {
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Success";
    }

    @Override
    public String AddTpIds(List<Object> tracepointIds) {
        for (int tracepointId : (List<Integer>) (List<?>) tracepointIds) {
            this.tf.addTracepoint(tracepointId);
        }
        return "Success";
    }

    @Override
    public String RetEndOfStream(int milliseconds) {
        long time_diff;
        do {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
		        System.exit(19);
            }
            time_diff = System.currentTimeMillis() - timeLastRecvdTuple;
        } while (time_diff < milliseconds || timeLastRecvdTuple == 0);
        return Long.toString(time_diff);
    }

    @Override
    public String TraceTuple(int tracepointId, List<String> traceArguments) {
        tf.traceEvent(tracepointId, traceArguments.toArray());
        return "Success";
    }

    @Override
    public String Configure() {
        System.out.println("Configure");
        // We only add schemas once
        isRuntimeActive = true;
        //File configFile = new File("/home/espen/Research/PhD/Private-WIP/stream-processing-benchmark-wip/" +
        //        "Experiments/experiment-configurations/SPE-specific-files/esper/movsim.esper.cfg.xml");
        //config.configure(configFile);

        // This line caused batch windows to fail
        //config.getRuntime().getThreading().setInternalTimerEnabled(false);

        //config.getRuntime().getThreading().setListenerDispatchPreserveOrder(false);
        runtime = EPRuntimeProvider.getRuntime("EsperExperimentFramework", config);
        runtime.initialize();
        eventService = runtime.getEventService();
        compiler = EPCompilerProvider.getCompiler();
        // types and variables shall be available for other statements
        config.getCompiler().getByteCode().setAccessModifierEventType(NameAccessModifier.PUBLIC);
        config.getCompiler().getByteCode().setAccessModifierVariable(NameAccessModifier.PUBLIC);

        // types shall be available for "sendEvent" use
        config.getCompiler().getByteCode().setBusModifierEventType(EventTypeBusModifier.BUS);

        args = new CompilerArguments(config);
        args.getPath().add(runtime.getRuntimePath());
        return "Success";
    }

    public static void main(String[] args) {
        boolean continue_running = true;
        while (continue_running) {
            EsperExperimentFramework experimentAPI = new EsperExperimentFramework();
            SpeComm speComm = new SpeComm(args, experimentAPI);
            experimentAPI.SetupClientTcpServer(speComm.GetClientPort());
            experimentAPI.SetTraceOutputFolder(speComm.GetTraceOutputFolder());
            speComm.AcceptTasks();
        }
    }
}
