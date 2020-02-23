import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		String urlLink = "https://www.polovniautomobili.com/auto-oglasi/pretraga?page=1&sort=basic&city_distance=0&showOldNew=all&without_price=1";
		
		
		for (int i = 1; i <= 100; i++) {
			try {
				urlLink = "https://www.polovniautomobili.com/auto-oglasi/pretraga?page=" + i + "&sort=basic&city_distance=0&showOldNew=all&without_price=1";
				getAllCarsFromSearch(urlLink);
				System.out.println("///////////////////////////////////////");
				System.out.println("ZAVRSENA STRANICA = " + i);
				System.out.println("///////////////////////////////////////");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		// getAutoFromLink(urlString);

		// getAllBrands();

		//getAllCarsFromMainPage();

		
	}

	///////////////////////////////////////////////////////////
	public static void getAutoFromLink(String urlString) throws Exception {
		URL url = new URL(urlString);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("automobili.txt"), true), true);
		Automobil automobil = new Automobil();
		int brojac = 1;
		List<String> listaSpecifikacija = new ArrayList<String>();
		String cena = "";
		boolean zaCenu = false;

		while (scanner.hasNext()) {
			try {
				line = scanner.nextLine();
				// System.out.println(line);
				if (line.contains("price-item position-relative")) {
					zaCenu = true;
					cena = scanner.nextLine().trim();
					String[] cenaSplit = cena.split(" ");
					if (cenaSplit[1].length() < 4) {
						int endIndex = cena.indexOf(" ");
						cena = cena.substring(0, endIndex);
					} else {
						System.out.println("MORA S CENOM");
						return;
					}
				}

				if (line.contains("uk-width-large-1-1 uk-width-medium-3-10 uk-width-1-2")) {
					for (int i = 0; i < 26; i++) {
						if (line.length() > 3) {
							if (line.contains("Karoserija")) {
								line = scanner.nextLine();
								line = scanner.nextLine();
								brojac += 2;
								continue;
							}
							int beginIndex = line.indexOf(">") + 1;
							int endIndex = line.indexOf("<", 5);

							// System.out.println(line.trim());
							if (brojac % 2 != 0 && beginIndex > 0 && endIndex > 0) {
								listaSpecifikacija.add(line.substring(beginIndex, endIndex));
							}
							// System.out.println(line.substring(beginIndex, endIndex));
							brojac++;
						}
						line = scanner.nextLine();
					}
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		if (!zaCenu) {
			scanner.close();
			writer.close();
			System.out.println("IZVNI BAKI, NE MOZE JOS S POPUSTOM");
			return;
		}

		automobil.stanje = listaSpecifikacija.get(0).trim();
		automobil.marka = listaSpecifikacija.get(1).trim();
		automobil.model = listaSpecifikacija.get(2).trim();
		automobil.godiste = listaSpecifikacija.get(3).trim();
		automobil.kilometraza = listaSpecifikacija.get(4).trim();
		automobil.gorivo = listaSpecifikacija.get(5).trim();
		automobil.kubikaza = listaSpecifikacija.get(6).trim();
		automobil.ks = listaSpecifikacija.get(7).trim();
		automobil.cena = cena;

		if (automobil.stanje.equals("Polovno vozilo")) {
			automobil.stanje = "Polovan";
		} else {
			automobil.stanje = "Nov";
		}

		automobil.godiste = automobil.godiste.substring(0, 4);

		int kubikazaEndIndex = automobil.kubikaza.indexOf(" ");
		automobil.kubikaza = automobil.kubikaza.substring(0, kubikazaEndIndex);

		int kilometrazaEndIndex = automobil.kilometraza.indexOf(" ");
		automobil.kilometraza = automobil.kilometraza.substring(0, kilometrazaEndIndex);
		if (automobil.kilometraza.contains(".")) {
			automobil.kilometraza = automobil.kilometraza.replace(".", "");
		}

		int ksEndIndex = automobil.ks.indexOf(" ");
		int ksBeginIndex = automobil.ks.indexOf("/");
		automobil.ks = automobil.ks.substring(ksBeginIndex + 1, ksEndIndex);

		if (automobil.cena.contains(".")) {
			automobil.cena = automobil.cena.replace(".", "");
		}

		System.out.println(automobil);

		writer.append(
				"INSERT INTO automobil (marka, model, godiste, kubikaza, kilometraza, ks, gorivo, stanje, cena)  VALUES (\""
						+ automobil.marka + "\", ");
		writer.append("\"" + automobil.model + "\", ");
		writer.append("\"" + automobil.godiste + "\", ");
		writer.append("\"" + automobil.kubikaza + "\", ");
		writer.append("\"" + automobil.kilometraza + "\", ");
		writer.append("\"" + automobil.ks + "\", ");
		writer.append("\"" + automobil.gorivo + "\", ");
		writer.append("\"" + automobil.stanje + "\", ");
		writer.append("\"" + automobil.cena + "\");\n");
		writer.append("\n");

		writer.close();
		scanner.close();
	}

	///////////////////////////////////////////////////////////
	public static void getAllCarsFromMainPage() throws Exception {
		URL url = new URL("https://www.polovniautomobili.com/");
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		List<String> linkovi = new ArrayList<>();

		while (scanner.hasNext()) {
			line = scanner.nextLine().trim();

			if (line.contains("uk-width-medium-1-4 uk-width-1-2 uk-padding-remove")) {
				String potrebnaLinija = scanner.nextLine().trim();
				int beginIndex = potrebnaLinija.indexOf("/");
				int endIndex = potrebnaLinija.indexOf(" ", 5);
				String link = potrebnaLinija.substring(beginIndex, endIndex - 1);
				link = "https://www.polovniautomobili.com" + link;
				linkovi.add(link);
				continue;
			}
		}

		scanner.close();

		for (String link : linkovi) {
			getAutoFromLink(link);
		}
	}
	
	///////////////////////////////////////////////////////////
	public static void getAllCarsFromSearch(String urlLink) throws Exception { 
		URL url = new URL(urlLink);
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		List<String> linkovi = new ArrayList<>();
		String link;
		int brojac = 0;
		
		while (scanner.hasNext()) {
			line = scanner.nextLine().trim();
			if(line.contains("<a onclick=\"dataLayer.push({'event':'klikIstaknut'});") && (brojac % 2 == 0)) {
				brojac++;
				//System.out.println(line);
				
				int beginIndex = line.indexOf("/");
				int endIndex = line.indexOf(" ", beginIndex + 1);
				link = line.substring(beginIndex, endIndex - 1);
				link = "https://www.polovniautomobili.com" + link;
				linkovi.add(link);
				
				line = scanner.nextLine().trim();
				
			}
			if(line.contains("<a onclick=\"dataLayer.push({'event':'klikIstaknut'});") && (brojac % 2 != 0)) {
				brojac++;
			}
			
			
		}
		scanner.close();
		
		for (String l : linkovi) {
			getAutoFromLink(l);
		}
		
	}

	///////////////////////////////////////////////////////////
	public static void getAllBrands() throws Exception {
		URL url = new URL("https://www.polovniautomobili.com/");
		Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
		String line;
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("marke.txt"), true), true);

		while (scanner.hasNext()) {
			line = scanner.nextLine();

			if (line.contains("audi")) {
				String[] brandsList = line.split("option");
				int brojac = 0;
				for (String string : brandsList) {
					if (brojac < 3) {
						brojac++;
						continue;
					}
					String linija = string.trim();
					int beginIndex = linija.indexOf(">", 5);
					int endIndex = linija.indexOf("<");
					if (endIndex > 0 && beginIndex > 0) {
						String marka = linija.substring(beginIndex + 1, endIndex);
						// System.out.println(marka);
						writer.append(marka + "\n");
					}
					if (string.contains("ostalo")) {
						break;
					}
				}
			}

		}
		writer.close();
		scanner.close();

	}

}
