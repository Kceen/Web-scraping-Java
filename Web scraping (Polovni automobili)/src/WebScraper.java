import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;

public class WebScraper {
	static int globalIdCounter = 0;
	
	////////////////////////////////////////
	public static void main(String[] args) {
			try {
				//getSingleCar("https://www.polovniautomobili.com/auto-oglasi/12133591/ford-ranger?ref=featured-home");
				
				getCarsFromSearch("https://www.polovniautomobili.com/auto-oglasi/pretraga?brand=&price_from=&price_to=&year_from=&year_to=&flywheel=&atest=&door_num=&submit_1=&without_price=1&date_limit=&showOldNew=all&modeltxt=&engine_volume_from=&engine_volume_to=&power_from=&power_to=&mileage_from=&mileage_to=&emission_class=&seat_num=&wheel_side=&registration=&country=&country_origin=&city=&registration_price=&page=&sort=");
				
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	////////////////////////////////////////
	
	public static void getSingleCar(String urlString) throws Exception {
		URL url = new URL(urlString);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("automobili.txt"), true), true);
		
		HashMap<String, Object> basicInfo = new HashMap<String, Object>();
		ArrayList<String> characteristics = new ArrayList<String>();
		ArrayList<String> security = new ArrayList<String>();
		ArrayList<String> equipment = new ArrayList<String>();
		ArrayList<String> condition = new ArrayList<String>();
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> imagesLinks = new ArrayList<String>();
		
		StringBuilder characteristicsStringBuilder = new StringBuilder();
		StringBuilder securityStringBuilder = new StringBuilder();
		StringBuilder equipmentStringBuilder = new StringBuilder();
		StringBuilder conditionStringBuilder = new StringBuilder();
		StringBuilder descriptionStringBuilder = new StringBuilder();
		
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
		String imagesFolderName = new String();
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
		//System.out.println(imagesLinks);
		
		
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
		
	
		System.out.println(basicInfo);
		/*
		System.out.println("////////////////////////////////////////////////////////");
		System.out.println("////////////////////////////////////////////////////////");
		System.out.println("////////////////////////////////////////////////////////");
		*/
		
		// MAKE ARRAYS INTO STRINGS
		for (String c : characteristics) {
			characteristicsStringBuilder.append(c + ",");
		}
		for (String s : security) {
			securityStringBuilder.append(s + ",");
		}
		for (String e : equipment) {
			equipmentStringBuilder.append(e + ",");
		}
		for (String cd : condition) {
			conditionStringBuilder.append(cd + ",");
		}
		for (String d : description) {
			descriptionStringBuilder.append(d + ",");
		}
		
		writer.append("INSERT INTO automobil(idAutomobil, marka, model, gorivo, ks, cena, kubikaza, zamena, kilometraza, fiksnaCena, stanje, karoserija, godiste, karakteristike, sigurnost, oprema, stanjeLista, opis, thumbnailPath) VALUES (");
		writer.append(++globalIdCounter + ",");
		writer.append("\"" + basicInfo.get("Marka").toString() + "\", ");
		writer.append("\"" + basicInfo.get("Model").toString() + "\", ");
		writer.append("\"" + basicInfo.get("Gorivo").toString() + "\", ");
		writer.append(basicInfo.get("Snaga motora").toString() + ",");
		writer.append(basicInfo.get("Cena").toString() + ",");
		writer.append(basicInfo.get("Kubikaža").toString() + ",");
		writer.append("\"" + basicInfo.get("Zamena").toString() + "\", ");
		writer.append(basicInfo.get("Kilometraža").toString() + ",");
		writer.append("\"" + basicInfo.get("Fiksna cena").toString() + "\", ");
		writer.append("\"" + basicInfo.get("Vozilo").toString() + "\", ");
		writer.append("\"" + basicInfo.get("Karoserija").toString() + "\", ");
		writer.append(basicInfo.get("Godište").toString() + ",");
		writer.append("\"" + characteristicsStringBuilder + "\", ");
		writer.append("\"" + securityStringBuilder + "\", ");
		writer.append("\"" + equipmentStringBuilder+ "\", ");
		writer.append("\"" + conditionStringBuilder + "\", ");
		writer.append("\"" + descriptionStringBuilder + "\", ");
		writer.append(null + ");");
		writer.append("\n");
		writer.append("\n");
	
		imagesFolderName = basicInfo.get("Marka").toString() + "-" + basicInfo.get("Model").toString() + "-" + basicInfo.get("Godište") + "-" + basicInfo.get("Kilometraža").toString();
		
		// DOWNLOADING THUMBNAIL
		imagePath = downloadImage(imagesLinks.get(0), imagesFolderName, true);
		writer.append("INSERT INTO slika(path, Automobil_idAutomobil) VALUES (");
		writer.append("\"/autinjo/images/" + imagePath + "\", ");
		writer.append(globalIdCounter + ");");
		writer.append("\n");
		writer.append("UPDATE `autinjodb`.`automobil` SET `thumbnailPath` = " + "\"/autinjo/images/" + imagePath + "\"" + " WHERE (`idAutomobil` = " + globalIdCounter + ");");
		writer.append("\n");
		writer.append("\n");
		
		
		
		// DOWNLOADING ALL IMAGES
		//for (String imgLink : imagesLinks) {
		
		// DOWNLOADING FIRST 5 IMAGES
		for(int i = 0 ; i < 5 ; i++) {
			imagePath = downloadImage(imagesLinks.get(i), imagesFolderName, false);
			writer.append("INSERT INTO slika(path, Automobil_idAutomobil) VALUES (");
			writer.append("\"/autinjo/images/" + imagePath + "\", ");
			writer.append(globalIdCounter + ");");
			writer.append("\n");
			writer.append("\n");
		}
		System.out.println("CAR DONE");
		 
		writer.close();
		scanner.close();
	}
	
	public static void getCarsFromSearch(String urlString) throws Exception {
		URL url = new URL(urlString);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		Set<String> linksSet = new HashSet<String>();
		
		// GATHER ALL LINKS LEADING TO INDIVIDUAL CAR PAGE
		while(scanner.hasNext()) {
			line = scanner.nextLine().trim();
			
			if(line.contains("dataLayer.push({'event':'klikIstaknut'});")) {
				if(line.contains("title")) {
					line = line.substring(line.indexOf("href=") + 7, line.lastIndexOf(" title") - 1);
					line = "https://www.polovniautomobili.com/" + line;
				}
				else {
					line = line.substring(line.indexOf("href=") + 7, line.lastIndexOf(">") - 1);
					line = "https://www.polovniautomobili.com/" + line;
				}
			
				linksSet.add(line);
			}
			if(line.contains("dataLayer.push({'event':'klikIstaknutXl'});") && !line.contains("title")) {
				if(line.contains("image")) {
					line = line.substring(line.indexOf("href=") + 7, line.lastIndexOf("?"));
					line = "https://www.polovniautomobili.com/" + line;
				}
				else {
					line = line.substring(line.indexOf("href=") + 7, line.lastIndexOf(">") - 1);
					line = "https://www.polovniautomobili.com/" + line;
				}
				
				linksSet.add(line);
			}
			if(line.contains("dataLayer.push({'event':'klikVrh'});")) {
				line = line.substring(line.indexOf("href=") + 7, line.indexOf("?"));
				line = "https://www.polovniautomobili.com/" + line;
				
				linksSet.add(line);
			}
		}
		
		
		for (String link : linksSet) {
			getSingleCar(link);
		}
		
		
		scanner.close();
	
	}

	// DOWNLOAD THE IMAGE FROM GIVEN URL AND RETURN PATH ON DISK WHERE IT IS DOWNLOADED
	public static String downloadImage(String imageLinkString, String imagesFolderName, boolean thumbnail) {
		BufferedImage image = null;
		Random random = new Random();
		int randomImageInt = random.nextInt(1000);
		
		try {
		    URL imageUrl = new URL(imageLinkString);
		    image = ImageIO.read(imageUrl);
		    
		    Path path = Paths.get("C:\\Users\\Nikola\\Desktop\\Java Workspace\\Ostalo\\AutinjoSPRING\\src\\main\\webapp\\images\\" + imagesFolderName);
		    Files.createDirectories(path);
		    
		    if(thumbnail) {
		    	ImageIO.write(image, "jpg", new File(path + "\\thumbail.jpg"));
		    	return imagesFolderName + "/thumbail.jpg";
		    }
		    else {
		    	ImageIO.write(image, "jpg", new File(path + "\\" + randomImageInt + ".jpg"));
		    	return imagesFolderName + "/" +  randomImageInt + ".jpg";
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
			return "EXCEPTION OCCURED";
		}
	}

}
