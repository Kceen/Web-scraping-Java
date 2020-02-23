import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class WebScraper {

	////////////////////////////////////////
	public static void main(String[] args) {
		int i = 0;
		while(i < 10) {
			try {
				//getSingleCar("https://www.polovniautomobili.com/auto-oglasi/15863033/volkswagen-passat-cc");
				
				getCarsFromSearch("https://www.polovniautomobili.com/auto-oglasi/pretraga?brand=&price_to=&year_from=&year_to=&showOldNew=all&submit_1=&without_price=1");
				i++;
			
			} catch (Exception e) {
				e.printStackTrace();
				i++;
			}
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
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> imagesLinks = new ArrayList<String>();
		
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
		String descriptionLine = new String();
		String imageLink = new String();
		String imagePath = new String();
		
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			line = line.trim();
			
			// BASIC INFO NAME
			if(line.contains("uk-hidden-large uk-width-medium-2-10 uk-width-1-2 uk-text-bold")) {
				int basicInfoNameBeginIndex = line.indexOf(">", 5) + 1;
				int basicInfoNameEndIndex = line.indexOf("</");
				
				// IF CAR CHANGING IS YES FOR CHEAPER CARS AND A LINK
				if(line.contains("</a>")) {
					basicInfoNameBeginIndex = line.lastIndexOf(";\">") + 3;
					basicInfoNameEndIndex = line.lastIndexOf("</a>");
				}
				
				String basicInfoName = line.substring(basicInfoNameBeginIndex, basicInfoNameEndIndex).trim();
				if(basicInfoName.endsWith(":")) {
					basicInfoName = basicInfoName.substring(0, basicInfoName.length() - 1);
				}
				
				hasBasicInfoName = true;
				forBasicInfoMapName = basicInfoName;
				
			}
			
			// BASIC INFO VALUE
			if(line.contains("uk-width-large-1-1 uk-width-medium-3-10 uk-width-1-2")) {
				int basicInfoValueBeginIndex = line.lastIndexOf("\">") + 2;
				int basicInfoValueEndIndex = line.indexOf("</div>");
				
				if(basicInfoValueEndIndex == -1) {
					line = scanner.nextLine();
					basicInfoValueEndIndex = line.indexOf("</div>");
				}
				
				// IF CAR CHANGING IS YES FOR CHEAPER CARS AND A LINK
				if(line.contains("</a>")) {
					basicInfoValueBeginIndex = line.lastIndexOf("</span> ") + 6;
					basicInfoValueEndIndex = line.lastIndexOf("</a>");
				}
				
				String basicInfoValue = line.substring(basicInfoValueBeginIndex, basicInfoValueEndIndex);
				
				hasBasicInfoValue = true;
				forBasicInfoMapValue = basicInfoValue;
			}
			
			// PRICE
			if(line.contains("price-item position-relative")) {
				price = scanner.nextLine().trim();
			}
			if(line.contains("price-item-discount position-relative")) {
				price = scanner.nextLine().trim();
				price = scanner.nextLine().trim();
			}
			
			// INFO CATEGORY NAME
			if(line.contains("classified-title js-title-toggle position-relative")) {
				category = line.substring(line.indexOf("relative\">") + 10, line.indexOf("<i")).trim();
			}
			
			// SECURITY, EQUIPMENT AND CONDITION
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
			
			// DESCRIPTION
			if(line.contains("uk-width-1-1 description-wrapper")) {
				descriptionLine = scanner.nextLine().trim();
				while(!descriptionLine.equals("</div>")) {
					if(descriptionLine.contains("<br />")) {
						descriptionLine = descriptionLine.substring(0, descriptionLine.indexOf("<br />"));
					}
					description.add(descriptionLine);
					descriptionLine = scanner.nextLine().trim();
					
				}
			}
			
			// IMAGES
			if(line.contains("<li data-thumb")) {
				imageLink = line.substring(line.lastIndexOf("https://images3"), line.lastIndexOf(" class") - 1);
				imagesLinks.add(imageLink);
			}
				
			
			
			if(hasBasicInfoName && hasBasicInfoValue) {
				basicInfo.put(forBasicInfoMapName, forBasicInfoMapValue);
			}
			
			
		}
		
		
		System.out.println(characteristics);
		System.out.println(security);
		System.out.println(equipment);
		System.out.println(condition);
		System.out.println(description);
		System.out.println(imagesLinks);
		
		
		// FIXING BASIC INFO VALUES (GOOD FORMATTING)
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
		
		imagePath = basicInfo.get("Marka").toString() + "-" + basicInfo.get("Model").toString() + "-" + basicInfo.get("Godište") + "-" + basicInfo.get("Kilometraža").toString();
		for (String imgLink : imagesLinks) {
			downloadImage(imgLink, imagePath);
		}
		
		System.out.println(basicInfo);
		System.out.println("////////////////////////////////////////////////////////");
		System.out.println("////////////////////////////////////////////////////////");
		System.out.println("////////////////////////////////////////////////////////");
		
		scanner.close();
	}
	
	public static void getCarsFromSearch(String urlString) throws Exception {
		URL url = new URL(urlString);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		List<String> links = new ArrayList<>();
		String link;
		int counter = 0;
		
		while (scanner.hasNext()) {
			line = scanner.nextLine().trim();
			if(line.contains("<a onclick=\"dataLayer.push({'event':'klikIstaknut'});") && (counter % 2 == 0)) {
				counter++;
				//System.out.println(line);
				
				int beginIndex = line.indexOf("/");
				int endIndex = line.indexOf(" ", beginIndex + 1);
				link = line.substring(beginIndex, endIndex - 1);
				link = "https://www.polovniautomobili.com" + link;
				links.add(link);
				
				line = scanner.nextLine().trim();
				
			}
			if(line.contains("<a onclick=\"dataLayer.push({'event':'klikIstaknut'});") && (counter % 2 != 0)) {
				counter++;
			}
			
			
		}
		scanner.close();
		
		for (String l : links) {
			getSingleCar(l);
		}
	
	}

	public static void downloadImage(String imageLinkString, String imagePath) {
		BufferedImage image = null;
		Random random = new Random();
		try {
		    URL imageUrl = new URL(imageLinkString);
		    image = ImageIO.read(imageUrl);
		    
		    Path path = Paths.get("C:\\Users\\Nikola\\git\\Web-scraping-Java\\Web scraping (Polovni automobili)\\" + imagePath);
		    Files.createDirectories(path);
		    
		    ImageIO.write(image, "jpg", new File(path + "\\" + random.nextInt(1000) + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
