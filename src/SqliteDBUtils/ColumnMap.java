package SqliteDBUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finance.Logger;

public class ColumnMap<K, V> {
	Map<String, String> columnMap;
	
	public ColumnMap(String filename) throws IOException {
		readMap(filename);
	}
	
	public Set<String> keySet() {
		return columnMap.keySet();
	}
	
	
	private Map<String, String> readMap(String filename) throws IOException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<String> list = Files.readAllLines(Paths.get(filename));
		Pattern p = Pattern.compile("((\\,|[^,])+),([^#]*)");
		
		for (int i = 0; i < list.size(); i++) {
			// analyze strings something like this:
			// "((\\,|[^,])+),([^#]*)"  we want groups 0,2 I think
			
			// find the strings to map and put them in the map
			Logger.out.println("scanning line: " + list.get(i));
			Matcher m = p.matcher(list.get(i));
			m.matches();
			
			int groupCount = m.groupCount();
			Logger.out.println("number of groups: " + groupCount);
			//group 1 & group 3 are our Key & Value
			map.put(m.group(1),m.group(3));

		} 
		this.columnMap = map;
		return(map);
	}
	
	public void printMap() {
		for (String key : columnMap.keySet()) {
			Logger.out.println("key: " + key + "\t\tvalue: " + columnMap.get(key));
		}
	}

	public String get(String key) {
		return columnMap.get(key);
	}
	
}

