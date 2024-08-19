package net.whydah.statistics;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class HealthReport{

	Map<String, Function<String, String>> registeredMap = new HashMap<String, Function<String, String>>();
	Map<String, String> reportMap = new HashMap<String, String>();
	Map<String, String> keyNameDisplayList = new HashMap<String, String>();
	Set<String> refreshList = new HashSet<String>();
	private String group, artifact, instanceName = "";
	
	public HealthReport(String group, String artifact, String instanceName) {
		this.group = group;
		this.artifact = artifact;
		this.instanceName = instanceName;
	}
	
	public HealthReport put(String keyName, Function<String, String> func) {
		registeredMap.put(keyName, func);
		reportMap.put(keyName, func.apply(keyName));
		return this;
	}
	
	public HealthReport put(String keyName, String keyNameDisplay, Function<String, String> func) {	
		keyNameDisplayList.put(keyName, keyNameDisplay);
		return put(keyName, func) ;
	}
	
	public HealthReport put(String keyName, String value) {
		reportMap.put(keyName, value);
		return this;
	}
	
	public HealthReport put(String keyName, String keyNameDisplay, String value) {
		keyNameDisplayList.put(keyName, keyNameDisplay);
		return put(keyName, value);
	}
	
	public void recompute(String name) {
		refreshList.add(name);
	}
	
	public String getReport() {
		
		refreshList.forEach(i->{
			if(registeredMap.containsKey(i)) {
				reportMap.put(i, null);	
			}
		});
		return render();
		
		
	}

	private String render() {
		refreshList.forEach(i -> {
			reportMap.compute(i, (k, v) -> v == null && registeredMap.containsKey(k) ? String.valueOf(registeredMap.get(k).apply(k)) : v);
		});
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append(" \"").append("version").append("\"").append(":").append("\"").append(getVersion()).append("\"").append(",\n");
		sb.append(" \"").append("name").append("\"").append(":").append("\"").append(instanceName).append("\"").append(",\n");
		sb.append(" \"").append("now").append("\"").append(":").append("\"").append(Instant.now()).append("\"").append(",\n");
		sb.append(" \"").append("ip").append("\"").append(":").append("\"").append(getMyIPAddresssesString()).append("\"").append(",\n");
		sb.append(" \"").append("running_since").append("\"").append(":").append("\"").append(getRunningSince()).append("\"").append(",\n");
		for (String k : reportMap.keySet()) {
			sb.append(" \"").append((keyNameDisplayList.containsKey(k) ? keyNameDisplayList.get(k) : k)).append("\"").append(":")
					.append("\"").append(reportMap.get(k)).append("\"").append(",\n");
		}
		String trmpstring = sb.toString();
		String resultString = trmpstring.substring(0, trmpstring.length() - 2) + "}\n";
		sb.append("}\n");
		return resultString; //sb.toString();
	}
	
	public static String getRunningSince() {
		long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
		return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString();
	}
	
	public String getMyIPAddresssesString() {
		String ipAdresses = "";

		try {
			ipAdresses = InetAddress.getLocalHost().getHostAddress();
			Enumeration n = NetworkInterface.getNetworkInterfaces();

			while (n.hasMoreElements()) {
				NetworkInterface e = (NetworkInterface) n.nextElement();

				InetAddress addr;
				for (Enumeration a = e.getInetAddresses(); a.hasMoreElements(); ipAdresses = ipAdresses + "  " + addr.getHostAddress()) {
					addr = (InetAddress) a.nextElement();
				}
			}
		} catch (Exception var5) {
			ipAdresses = "Not resolved";
		}

		return ipAdresses;
	}
	
	public synchronized String getVersion() {
	    String version = null;

	    // try to load from maven properties first
	    try {
	        Properties p = new Properties();
	        InputStream is = getClass().getResourceAsStream("/META-INF/maven/" + this.group +  "/" + this.artifact + "/pom.properties");
	        if (is != null) {
	            p.load(is);
	            version = p.getProperty("version", "");
	        }
	    } catch (Exception e) {
	        // ignore
	    }

	    // fallback to using Java API
	    if (version == null) {
	        Package aPackage = getClass().getPackage();
	        if (aPackage != null) {
	            version = aPackage.getImplementationVersion();
	            if (version == null) {
	                version = aPackage.getSpecificationVersion();
	            }
	        }
	    }

	    if (version == null) {
	        // we could not compute the version so use a blank
	        version = "";
	    }

	    return version;
	} 
	 

}
