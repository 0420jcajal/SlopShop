package com.example.slopshop.Usuario.Utils

object UbicacionesData {

    val paises = listOf(
        "Seleccione país",
        "España",
        "México",
        "Argentina",
        "Colombia",
        "Chile",
        "Perú",
        "Portugal",
        "Francia",
        "Italia",
        "Alemania",
        "Países Bajos",
        "Bélgica",
        "Polonia",
        "Grecia",
        "Suecia",
        "Finlandia",
        "Noruega",
        "Dinamarca",
        "Irlanda",
        "Reino Unido"
    )

    val provincias = mapOf(
        "España" to listOf(
            "Seleccione provincia",
            "Madrid",
            "Barcelona",
            "Valencia",
            "Sevilla",
            "Zaragoza",
            "Málaga",
            "Murcia",
            "Palma",
            "Bilbao",
            "Alicante"
        ),
        "México" to listOf("Seleccione provincia", "CDMX", "Guadalajara", "Monterrey", "Puebla"),
        "Argentina" to listOf("Seleccione provincia", "Buenos Aires", "Córdoba", "Santa Fe", "Mendoza"),
        "Colombia" to listOf("Seleccione provincia", "Bogotá", "Antioquia", "Valle del Cauca", "Cundinamarca"),
        "Chile" to listOf("Seleccione provincia", "Santiago", "Valparaíso", "Concepción", "La Serena"),
        "Perú" to listOf("Seleccione provincia", "Lima", "Arequipa", "Cusco", "Trujillo"),
        "Portugal" to listOf("Seleccione provincia", "Lisboa", "Porto", "Braga", "Coímbra"),
        "Francia" to listOf("Seleccione provincia", "París", "Lyon", "Marsella", "Toulouse"),
        "Italia" to listOf("Seleccione provincia", "Roma", "Milán", "Nápoles", "Turín"),
        "Alemania" to listOf("Seleccione provincia", "Berlín", "Múnich", "Hamburgo", "Colonia"),
        "Países Bajos" to listOf("Seleccione provincia", "Ámsterdam", "Rotterdam", "La Haya", "Utrecht"),
        "Bélgica" to listOf("Seleccione provincia", "Bruselas", "Amberes", "Gante", "Charleroi"),
        "Polonia" to listOf("Seleccione provincia", "Varsovia", "Cracovia", "Łódź", "Wrocław"),
        "Grecia" to listOf("Seleccione provincia", "Atenas", "Salónica", "Patras", "Heraclión"),
        "Suecia" to listOf("Seleccione provincia", "Estocolmo", "Gotemburgo", "Malmö", "Uppsala"),
        "Finlandia" to listOf("Seleccione provincia", "Helsinki", "Espoo", "Tampere", "Vantaa"),
        "Noruega" to listOf("Seleccione provincia", "Oslo", "Bergen", "Trondheim", "Stavanger"),
        "Dinamarca" to listOf("Seleccione provincia", "Copenhague", "Aarhus", "Odense", "Aalborg"),
        "Irlanda" to listOf("Seleccione provincia", "Dublín", "Cork", "Galway", "Limerick"),
        "Reino Unido" to listOf("Seleccione provincia", "Londres", "Manchester", "Birmingham", "Glasgow")
        )


    val ciudades = mapOf(

        "Madrid" to listOf(
            "Seleccione ciudad",
            "Madrid Centro",
            "Alcobendas",
            "Alcorcón",
            "Leganés",
            "Getafe",
            "Móstoles",
            "Fuenlabrada",
            "Torrejón de Ardoz",
            "Parla",
            "Coslada"
        ),

        "Barcelona" to listOf(
            "Seleccione ciudad",
            "Barcelona",
            "Badalona",
            "Hospitalet de Llobregat",
            "Sabadell",
            "Terrassa",
            "Mataró",
            "Santa Coloma de Gramenet",
            "Cornellà de Llobregat",
            "Sant Boi de Llobregat",
            "Rubí"
        ),

        "Valencia" to listOf(
            "Seleccione ciudad",
            "Valencia",
            "Gandía",
            "Torrent",
            "Paterna",
            "Mislata",
            "Sagunto",
            "Burjassot",
            "Alzira",
            "Ontinyent",
            "Xàtiva"
        ),

        "Sevilla" to listOf(
            "Seleccione ciudad",
            "Sevilla",
            "Dos Hermanas",
            "Alcalá de Guadaíra",
            "Utrera",
            "Écija",
            "La Rinconada",
            "Camas",
            "Mairena del Aljarafe",
            "Lebrija",
            "Los Palacios y Villafranca"
        ),

        "Zaragoza" to listOf(
            "Seleccione ciudad",
            "Zaragoza",
            "Utebo",
            "Cuarte de Huerva",
            "La Muela",
            "Ejea de los Caballeros",
            "Alagón",
            "Cascante",
            "Tauste",
            "Calatayud",
            "Caspe"
        ),

        "Málaga" to listOf(
            "Seleccione ciudad",
            "Málaga",
            "Marbella",
            "Fuengirola",
            "Torremolinos",
            "Estepona",
            "Rincón de la Victoria",
            "Benalmádena",
            "Mijas",
            "Nerja",
            "Antequera"
        ),

        "Murcia" to listOf(
            "Seleccione ciudad",
            "Murcia",
            "Cartagena",
            "Lorca",
            " Molina de Segura",
            "Yecla",
            "Cieza",
            "San Javier",
            "Mazarrón",
            "Alhama de Murcia",
            "Caravaca de la Cruz"
        ),

        "Palma" to listOf(
            "Seleccione ciudad",
            "Palma de Mallorca",
            "Manacor",
            "Inca",
            "Llucmajor",
            "Felanitx",
            "Alcúdia",
            "Santanyí",
            "Calvià",
            "Sóller",
            "Marratxí"
        ),

        "Bilbao" to listOf(
            "Seleccione ciudad",
            "Bilbao",
            "Barakaldo",
            "Getxo",
            "Portugalete",
            "Santurtzi",
            "Basauri",
            "Sestao",
            "Leioa",
            "Erandio",
            "Durango"
        ),

        "Alicante" to listOf(
            "Seleccione ciudad",
            "Alicante",
            "Elche",
            "Torrevieja",
            "Benidorm",
            "Orihuela",
            "San Vicente del Raspeig",
            "Elda",
            "Villena",
            "Alcoy",
            "Crevillente"
        ),

        // México
        "CDMX" to listOf("Seleccione ciudad", "Benito Juárez", "Coyoacán", "Iztapalapa", "Tlalpan", "Xochimilco"),
        "Guadalajara" to listOf("Seleccione ciudad", "Zapopan", "Tlaquepaque", "Tonala", "Tlajomulco", "El Salto"),
        "Monterrey" to listOf("Seleccione ciudad", "San Nicolás", "Guadalupe", "Apodaca", "Santa Catarina", "San Pedro"),
        "Puebla" to listOf("Seleccione ciudad", "Puebla", "Tehuacán", "Atlixco", "San Martín", "Cholula"),

        // Argentina
        "Buenos Aires" to listOf("Seleccione ciudad", "La Plata", "Mar del Plata", "Bahía Blanca", "Tandil", "Lomas de Zamora"),
        "Córdoba" to listOf("Seleccione ciudad", "Córdoba", "Villa Carlos Paz", "Río Cuarto", "Alta Gracia", "Cosquín"),
        "Santa Fe" to listOf("Seleccione ciudad", "Rosario", "Santa Fe", "Rafaela", "Venado Tuerto", "Esperanza"),
        "Mendoza" to listOf("Seleccione ciudad", "Mendoza", "San Rafael", "Godoy Cruz", "Luján de Cuyo", "Maipú"),

        // Colombia
        "Bogotá" to listOf("Seleccione ciudad", "Chapinero", "Usaquén", "Suba", "Engativá", "Fontibón"),
        "Antioquia" to listOf("Seleccione ciudad", "Medellín", "Envigado", "Bello", "Itagüí", "Rionegro"),
        "Valle del Cauca" to listOf("Seleccione ciudad", "Cali", "Palmira", "Buenaventura", "Cartago", "Tuluá"),
        "Cundinamarca" to listOf("Seleccione ciudad", "Soacha", "Girardot", "Zipaquirá", "Fusagasugá", "Chía"),

        // Chile
        "Santiago" to listOf("Seleccione ciudad", "Santiago Centro", "Puente Alto", "Maipú", "La Florida", "Las Condes"),
        "Valparaíso" to listOf("Seleccione ciudad", "Valparaíso", "Viña del Mar", "Quilpué", "Villa Alemana", "Concón"),
        "Concepción" to listOf("Seleccione ciudad", "Concepción", "Talcahuano", "Chillán", "Los Ángeles", "Coronel"),
        "La Serena" to listOf("Seleccione ciudad", "La Serena", "Coquimbo", "Ovalle", "Illapel", "Andacollo"),

        // Perú
        "Lima" to listOf("Seleccione ciudad", "Lima", "Miraflores", "San Isidro", "Surco", "San Borja"),
        "Arequipa" to listOf("Seleccione ciudad", "Arequipa", "Cayma", "Yanahuara", "José Luis Bustamante", "Hunter"),
        "Cusco" to listOf("Seleccione ciudad", "Cusco", "Santiago", "Wanchaq", "San Sebastián", "San Jerónimo"),
        "Trujillo" to listOf("Seleccione ciudad", "Trujillo", "Víctor Larco", "Florencia de Mora", "El Porvenir", "Salaverry"),

        // Portugal
        "Lisboa" to listOf("Seleccione ciudad", "Lisboa", "Amadora", "Oeiras", "Cascais", "Sintra"),
        "Porto" to listOf("Seleccione ciudad", "Porto", "Vila Nova de Gaia", "Matosinhos", "Maia", "Gondomar"),
        "Braga" to listOf("Seleccione ciudad", "Braga", "Guimarães", "Barcelos", "Fafe", "Vila Nova de Famalicão"),
        "Coímbra" to listOf("Seleccione ciudad", "Coímbra", "Cantanhede", "Mealhada", "Figueira da Foz", "Montemor-o-Velho"),

        // Francia
        "París" to listOf("Seleccione ciudad", "París", "Boulogne-Billancourt", "Saint-Denis", "Versalles", "Montreuil"),
        "Lyon" to listOf("Seleccione ciudad", "Lyon", "Villeurbanne", "Vénissieux", "Saint-Priest", "Caluire-et-Cuire"),
        "Marsella" to listOf("Seleccione ciudad", "Marsella", "Aix-en-Provence", "Arles", "Avignon", "Marignane"),
        "Toulouse" to listOf("Seleccione ciudad", "Toulouse", "Blagnac", "Montaudran", "Colomiers", "Muret"),

        // Italia
        "Roma" to listOf("Seleccione ciudad", "Roma", "Fiumicino", "Ostia", "Ciampino", "Tivoli"),
        "Milán" to listOf("Seleccione ciudad", "Milán", "Sesto San Giovanni", "Cinisello Balsamo", "Monza", "Legnano"),
        "Nápoles" to listOf("Seleccione ciudad", "Nápoles", "Caserta", "Salerno", "Avellino", "Benevento"),
        "Turín" to listOf("Seleccione ciudad", "Turín", "Moncalieri", "Nichelino", "Rivoli", "Settimo Torinese"),

        // Alemania
        "Berlín" to listOf("Seleccione ciudad", "Berlín", "Potsdam", "Pankow", "Spandau", "Friedrichshain"),
        "Múnich" to listOf("Seleccione ciudad", "Múnich", "Freising", "Erding", "Dachau", "Garching"),
        "Hamburgo" to listOf("Seleccione ciudad", "Hamburgo", "Lübeck", "Norderstedt", "Pinneberg", "Wedel"),
        "Colonia" to listOf("Seleccione ciudad", "Colonia", "Bonn", "Leverkusen", "Köln", "Pulheim"),

        // Países Bajos
        "Ámsterdam" to listOf("Seleccione ciudad", "Ámsterdam", "Haarlem", "Amstelveen", "Almere", "Zaandam"),
        "Rotterdam" to listOf("Seleccione ciudad", "Rotterdam", "Dordrecht", "Capelle aan den IJssel", "Schiedam", "Spijkenisse"),
        "La Haya" to listOf("Seleccione ciudad", "La Haya", "Leiden", "Delft", "Zoetermeer", "Rijswijk"),
        "Utrecht" to listOf("Seleccione ciudad", "Utrecht", "Nieuwegein", "Vianen", "Houten", "Zeist"),

        // Bélgica
        "Bruselas" to listOf("Seleccione ciudad", "Bruselas", "Brugge", "Leuven", "Namur", "Mons"),
        "Amberes" to listOf("Seleccione ciudad", "Amberes", "Mechelen", "Sint-Niklaas", "Turnhout", "Lier"),
        "Gante" to listOf("Seleccione ciudad", "Gante", "Aalst", "Sint-Truiden", "Hasselt", "Kortrijk"),
        "Charleroi" to listOf("Seleccione ciudad", "Charleroi", "La Louvière", "Mons", "Tournai", "Braine-le-Comte"),

        // Polonia
        "Varsovia" to listOf("Seleccione ciudad", "Varsovia", "Pruszków", "Piaseczno", "Radzymin", "Marki"),
        "Cracovia" to listOf("Seleccione ciudad", "Cracovia", "Tarnów", "Nowy Sącz", "Oświęcim", "Zakopane"),
        "Łódź" to listOf("Seleccione ciudad", "Łódź", "Piotrków Trybunalski", "Pabianice", "Bełchatów", "Zgierz"),
        "Wrocław" to listOf("Seleccione ciudad", "Wrocław", "Legnica", "Głogów", "Oleśnica", "Świdnica"),

        // Grecia
        "Atenas" to listOf("Seleccione ciudad", "Atenas", "Pireo", "Kallithea", "Glyfada", "Marousi"),
        "Salónica" to listOf("Seleccione ciudad", "Salónica", "Kalamaria", "Katerini", "Veria", "Kavala"),
        "Patras" to listOf("Seleccione ciudad", "Patras", "Aigio", "Akrata", "Kato Achaia", "Rio"),
        "Heraclión" to listOf("Seleccione ciudad", "Heraclión", "Agios Nikolaos", "Malia", "Hersonissos", "Anogia"),

        // Suecia
        "Estocolmo" to listOf("Seleccione ciudad", "Estocolmo", "Solna", "Sundbyberg", "Täby", "Nacka"),
        "Gotemburgo" to listOf("Seleccione ciudad", "Gotemburgo", "Mölndal", "Borås", "Alingsås", "Kungälv"),
        "Malmö" to listOf("Seleccione ciudad", "Malmö", "Lund", "Trelleborg", "Helsingborg", "Kristianstad"),
        "Uppsala" to listOf("Seleccione ciudad", "Uppsala", "Enköping", "Tierp", "Älvkarleby", "Knivsta"),

        // Finlandia
        "Helsinki" to listOf("Seleccione ciudad", "Helsinki", "Espoo", "Vantaa", "Kauniainen", "Kerava"),
        "Espoo" to listOf("Seleccione ciudad", "Espoo", "Kirkkonummi", "Järvenpää", "Tuusula", "Sipoo"),
        "Tampere" to listOf("Seleccione ciudad", "Tampere", "Nokia", "Ylöjärvi", "Pirkkala", "Lempäälä"),
        "Vantaa" to listOf("Seleccione ciudad", "Vantaa", "Kerava", "Sipoo", "Tuusula", "Järvenpää"),

        // Noruega
        "Oslo" to listOf("Seleccione ciudad", "Oslo", "Bærum", "Lillestrøm", "Drammen", "Fredrikstad"),
        "Bergen" to listOf("Seleccione ciudad", "Bergen", "Arna", "Fana", "Ytrebygda", "Åsane"),
        "Trondheim" to listOf("Seleccione ciudad", "Trondheim", "Meldal", "Melhus", "Orkdal", "Malvik"),
        "Stavanger" to listOf("Seleccione ciudad", "Stavanger", "Sandnes", "Sola", "Randaberg", "Klepp"),

        // Dinamarca
        "Copenhague" to listOf("Seleccione ciudad", "Copenhague", "Frederiksberg", "Gladsaxe", "Gentofte", "Lyngby-Taarbæk"),
        "Aarhus" to listOf("Seleccione ciudad", "Aarhus", "Randers", "Silkeborg", "Horsens", "Viborg"),
        "Odense" to listOf("Seleccione ciudad", "Odense", "Svendborg", "Nyborg", "Middelfart", "Kerteminde"),
        "Aalborg" to listOf("Seleccione ciudad", "Aalborg", "Frederikshavn", "Hjørring", "Brønderslev", "Thisted"),

        // Irlanda
        "Dublín" to listOf("Seleccione ciudad", "Dublín", "Cork", "Limerick", "Galway", "Waterford"),
        "Cork" to listOf("Seleccione ciudad", "Cork", "Waterford", "Kilkenny", "Tralee", "Killarney"),
        "Galway" to listOf("Seleccione ciudad", "Galway", "Ballinasloe", "Tuam", "Clifden", "Oughterard"),
        "Limerick" to listOf("Seleccione ciudad", "Limerick", "Ennis", "Nenagh", "Castlebar", "Listowel"),

        // Reino Unido
        "Londres" to listOf("Seleccione ciudad", "Londres", "Cambridge", "Oxford", "Brighton", "Reading"),
        "Manchester" to listOf("Seleccione ciudad", "Manchester", "Salford", "Stockport", "Bolton", "Oldham"),
        "Birmingham" to listOf("Seleccione ciudad", "Birmingham", "Coventry", "Solihull", "Wolverhampton", "Dudley"),
        "Glasgow" to listOf("Seleccione ciudad", "Glasgow", "Edimburgo", "Aberdeen", "Dundee", "Inverness")
    )
}
