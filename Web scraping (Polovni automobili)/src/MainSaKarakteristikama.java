import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainSaKarakteristikama {

	////////////////////////////////////////
	public static void main(String[] args) {
		try {
			getSingleCar("https://www.polovniautomobili.com/auto-oglasi/14687627/audi-a3?ref=featured-home");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	////////////////////////////////////////
	
	public static void getSingleCar(String urlString) throws Exception {
		URL url = new URL(urlString);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		//PrintWriter writer = new PrintWriter(new FileOutputStream(new File("automobili.txt"), true), true);
		
		HashMap<String, Object> basicInfo = new HashMap<String, Object>();
		ArrayList<String> characteristics = new ArrayList<String>();
		ArrayList<String> security = new ArrayList<String>();
		ArrayList<String> equipment = new ArrayList<String>();
		ArrayList<String> condition = new ArrayList<String>();
		
		String forBasicInfoMapName = new String();
		Object forBasicInfoMapValue = new Object();
		boolean hasBasicInfoName = false;
		boolean hasBasicInfoValue = false;
		String price = new String();
		String category = new String();
		String characteristicName = new String();
		String characteristicValue = new String();
		String characteristicWhole = new String();
		String securityValue = new String();
		String equipmentValue = new String();
		String conditionValue = new String();
		
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			line = line.trim();
			
			if(line.contains("uk-hidden-large uk-width-medium-2-10 uk-width-1-2 uk-text-bold")) {
				int basicInfoNameBeginIndex = line.indexOf(">", 5) + 1;
				int basicInfoNameEndIndex = line.indexOf("</");
				
				String basicInfoName = line.substring(basicInfoNameBeginIndex, basicInfoNameEndIndex).trim();
				if(basicInfoName.endsWith(":")) {
					basicInfoName = basicInfoName.substring(0, basicInfoName.length() - 1);
				}
				
				hasBasicInfoName = true;
				forBasicInfoMapName = basicInfoName;
				
			}
			if(line.contains("uk-width-large-1-1 uk-width-medium-3-10 uk-width-1-2")) {
				int basicInfoValueBeginIndex = line.lastIndexOf("\">") + 2;
				int basicInfoValueEndIndex = line.indexOf("</div>");
				
				if(basicInfoValueEndIndex == -1) {
					line = scanner.nextLine();
					basicInfoValueEndIndex = line.indexOf("</div>");
				}
				
				String basicInfoValue = line.substring(basicInfoValueBeginIndex, basicInfoValueEndIndex);
				
				hasBasicInfoValue = true;
				forBasicInfoMapValue = basicInfoValue;
			}
			if(line.contains("price-item position-relative")) {
				price = scanner.nextLine().trim();
			}
			if(line.contains("price-item-discount position-relative")) {
				price = scanner.nextLine().trim();
				price = scanner.nextLine().trim();
			}
			if(line.contains("classified-title js-title-toggle position-relative")) {
				category = line.substring(line.indexOf("relative\">") + 10, line.indexOf("<i")).trim();
				System.out.println(category);
			}
			
			// SECURITY AND EQUIPMENT
			if(line.contains("uk-width-medium-1-3 uk-width-1-2")) {
				if(category.equals("Sigurnost")) {
					securityValue = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
					security.add(securityValue);
				}
				if(category.equals("Oprema")) {
					equipmentValue = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
					equipment.add(equipmentValue);
				}
				if(category.equals("Stanje")) {
					conditionValue = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
					condition.add(conditionValue);
				}
			}
			
			// CHARACTERISTICS 
			if(line.contains("uk-width-medium-1-4 uk-width-1-2")) {
				characteristicName = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
				line = scanner.nextLine();
				characteristicValue = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
				
				characteristicWhole = characteristicName + ":" + characteristicValue;
				characteristics.add(characteristicWhole);
			}
			
			
			if(hasBasicInfoName && hasBasicInfoValue) {
				basicInfo.put(forBasicInfoMapName, forBasicInfoMapValue);
			}
			
			
		}
		
		
		System.out.println(basicInfo);
		System.out.println(characteristics);
		System.out.println(security);
		System.out.println(equipment);
		System.out.println(condition);
		
		String kilometraža = basicInfo.get("Kilometraža").toString();
		kilometraža = kilometraža.substring(0, kilometraža.length() - 3);
		kilometraža = kilometraža.replace(".", "");
		basicInfo.put("Kilometraža", kilometraža);
		
		String ks = basicInfo.get("Snaga motora").toString();
		ks = ks.substring(ks.indexOf("/") + 1, ks.indexOf(" "));
		basicInfo.put("Snaga motora", ks);
		
		String kubikaža = basicInfo.get("Kubikaža").toString();
		kubikaža = kubikaža.substring(0, kubikaža.indexOf(" cm"));
		basicInfo.put("Kubikaža", kubikaža);
		
		String atestiran = basicInfo.getOrDefault("Atestiran", "notTNG").toString();
		if(!atestiran.equalsIgnoreCase("notTNG")) {
			atestiran = atestiran.substring(atestiran.lastIndexOf("> ") + 2, atestiran.lastIndexOf("	") - 1);
			basicInfo.put("Atestiran", atestiran);
		}
		
		String zamena = basicInfo.get("Zamena").toString();
		zamena = zamena.substring(zamena.indexOf(" ") + 1, zamena.length());
		basicInfo.put("Zamena", zamena);
		
		String godište = basicInfo.get("Godište").toString();
		godište = godište.substring(0, 4);
		basicInfo.put("Godište", godište);
		
		if(price.equalsIgnoreCase("Po dogovoru")) {
			price = "0";
		}
		else if(price.equalsIgnoreCase("Na upit")) {
			price = "-1";
		}
		else {
			basicInfo.put("Cena", price);
			price = basicInfo.get("Cena").toString();
			price = price.substring(0, price.indexOf(" "));
			price = price.replace(".", "");
		}
		basicInfo.put("Cena", price);
		
		basicInfo.remove("Broj oglasa");
		System.out.println(basicInfo);
		
		scanner.close();
	}
}
