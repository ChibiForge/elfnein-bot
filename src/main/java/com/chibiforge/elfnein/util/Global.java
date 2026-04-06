package com.chibiforge.elfnein.util;

import java.util.HashMap;
import java.util.Map;

import com.chibiforge.elfnein.util.Locator.Location;

import discord4j.common.util.Snowflake;
import discord4j.core.object.emoji.Emoji;

public class Global {
	
	/////////
	// IDs //
	/////////
	
    public static String KarutaID = "646937666251915264";
    public static String ElfneinID = "1152674294124531732";
    
    ////////////////
    // CommandIDs //
    ////////////////
    
    // Admin
    public static Integer cmdAdmin = 100;
    
    // Date Solver
    public static Integer cmdIdSolve = 11;
    public static Integer cmdIdMap = 12;
    public static Integer cmdIdDate = 1;
    
    // Interactions
    public static Integer cmdIdInt = 2;
    
    // Activity
    public static Integer cmdIdActivity = 3;
    public static Integer cmdIdActivityRoles = 32;
    
    // Shogun
    public static Integer cmdIdLoop = 4;
    public static Integer cmdIdNodes = 4;
    public static Integer cmdIdTrack = 42;
    public static Integer cmdIdLoopTimer = 43;
    public static Integer cmdIdShogunInfo = 44; // Set
    public static Integer cmdIdNodeInfo = 45; // Set
    
    // Analysis
    public static Integer cmdIdDyeAnalysis = 51;
    
    // Utils
    public static Integer cmdIdSay = 101;
    public static Integer cmdIdRepeate = 102;
    public static Integer cmdIdMath = 201;
    
    // Games
    public static Integer cmdIdArea = 202;
    
    // Events
    public static Integer cmdIdEvent = 1000;
    public static Integer cmdIdChatting = 1001;
    
    public static Integer bleachEventAdmin = 300;
    public static Integer bleachEventBase = 301;
    public static Integer bleachEventSpawn = 302;
    
    public static Integer eventTOTAdmin = 310;
    public static Integer eventTOTBase = 311;
    public static Integer eventTOTDrop = 312;
    public static Integer eventTOTCmd = 313;

    public static Integer eventXmasAdmin = 320;
    public static Integer eventXmasBase = 321;
    public static Integer eventXmasCrafting = 322;
    
    
    /////////////
    
    //public static String loadingGIF = "https://tenor.com/view/cat-meow-loading-loading-paws-gif-5401992";
    public static String loadingGIF = "https://tenor.com/view/elfnein-loading-symphogear-gif-13711219472181706366";
    
    ////////////
    // Emojis //
    ////////////
    
    // Emotions
    public static String elf_sleep = "<:elf_sleep:1339635415849635962>";
    public static String elf_sleepy = "<:elf_sleepy:1339635429900812421>";
    public static String elf_cry = "<:elf_cry:1339635302335250544>";
    public static String elf_love = "<:elf_love:1339635327467261952>";
    public static String elf_huh = "<:elf_huh:1344442716536438784>";
    
    // Utils: Date
    public static String gas_east = "<:gas_east:1341108561912270941>";
    public static String gas_west = "<:gas_west:1341108529049895004>";
    public static String gas_north = "<:gas_north:1341108551678034052>";
    public static String gas_south = "<:gas_south:1341108539367755786>";
    
    // Utils: Nodes
    public static String node_gold = "<:node_gold:1364003931608449185>";
    public static String node_bone = "<:node_bone:1364003867729199122>";
    public static String node_clay = "<:node_clay:1364003879175327744>";
    public static String node_copper = "<:node_copper:1364003891347325009>";
    public static String node_essence = "<:node_essence:1364003904076910663>";
    public static String node_flower = "<:node_flower:1364003917180047483>";
    public static String node_ice = "<:node_ice:1364003942635143281>";
    public static String node_iron = "<:node_iron:1364003953175429191>";
    public static String node_leaf = "<:node_leaf:1364003964403585184>";
    public static String node_magma = "<:node_magma:1364003975938179092>";
    public static String node_oil = "<:node_oil:1364003988332089466>";
    public static String node_quartz = "<:node_quartz:1364004001208602704>";
    public static String node_salt = "<:node_salt:1364004014823440475>";
    public static String node_stone = "<:node_stone:1364004026324090940>";
    public static String node_sugar = "<:node_sugar:1364004039280300042>";
    public static String node_uranium = "<:node_uranium:1364004052396146738>";
    public static String node_wax = "<:node_wax:1364004066753122414>";
    public static String node_wood = "<:node_wood:1364006045013381325>";
    public static String node_wool = "<:node_wool:1364004079746945244>";
    public static String node_zinc = "<:node_zinc:1364004091210104893>";
    
    public static Map<String,String> nodes = new HashMap<>();
    
    static {
    	nodes.put("gold", node_gold);
    	nodes.put("bone", node_bone);
    	nodes.put("clay", node_clay);
    	nodes.put("copper", node_copper);
    	nodes.put("essence", node_essence);
    	nodes.put("flower", node_flower);
    	nodes.put("ice", node_ice);
    	nodes.put("iron", node_iron);
    	nodes.put("leaf", node_leaf);
    	nodes.put("magma", node_magma);
    	nodes.put("oil", node_oil);
    	nodes.put("quartz", node_quartz);
    	nodes.put("salt", node_salt);
    	nodes.put("stone", node_stone);
    	nodes.put("sugar", node_sugar);
    	nodes.put("uranium", node_uranium);
    	nodes.put("wax", node_wax);
    	nodes.put("wood", node_wood);
    	nodes.put("wool", node_wool);
    	nodes.put("zinc", node_zinc);
    }
    
    public static Map<String,String> nodesMono = new HashMap<>();
    
    static {
    	nodesMono.put("gold"	, "𝚐𝚘𝚕𝚍");
    	nodesMono.put("bone"	, "𝚋𝚘𝚗𝚎");
    	nodesMono.put("clay"	, "𝚌𝚕𝚊𝚢");
    	nodesMono.put("copper"	, "𝚌𝚘𝚙𝚙𝚎𝚛");
    	nodesMono.put("essence"	, "𝚎𝚜𝚜𝚎𝚗𝚌𝚎");
    	nodesMono.put("flower"	, "𝚏𝚕𝚘𝚠𝚎𝚛");
    	nodesMono.put("ice"		, "𝚒𝚌𝚎");
    	nodesMono.put("iron"	, "𝚒𝚛𝚘𝚗");
    	nodesMono.put("leaf"	, "𝚕𝚎𝚊𝚏");
    	nodesMono.put("magma"	, "𝚖𝚊𝚐𝚖𝚊");
    	nodesMono.put("oil"		, "𝚘𝚒𝚕");
    	nodesMono.put("quartz"	, "𝚚𝚞𝚊𝚛𝚝𝚣");
    	nodesMono.put("salt"	, "𝚜𝚊𝚕𝚝");
    	nodesMono.put("stone"	, "𝚜𝚝𝚘𝚗𝚎");
    	nodesMono.put("sugar"	, "𝚜𝚞𝚐𝚊𝚛");
    	nodesMono.put("uranium"	, "𝚞𝚛𝚊𝚗𝚒𝚞𝚖");
    	nodesMono.put("wax"		, "𝚠𝚊𝚡");
    	nodesMono.put("wood"	, "𝚠𝚘𝚘𝚍");
    	nodesMono.put("wool"	, "𝚠𝚘𝚘𝚕");
    	nodesMono.put("zinc"	, "𝚣𝚒𝚗𝚌");
    }
    
    
    /////////////////////
    // TriggerCommands //
    /////////////////////
    
    // Greatings
    public static String [] grT = {"hello", "heyo", "hi\n", "hi ", " hi"};
    public static String [] grA = {"Greatings summoner!", "Heyo heyo!", "Nice to met you!"};
    public static double grC = 0.5;
    
    // Good night
    public static String [] gnT = {"good night","going to sleep", "ima go sleep", "gonna go sleep","gotta go sleep","gotta go to sleep"}; 
	public static String [] gnA = {elf_sleepy,elf_sleep,"Good Night! " + elf_sleepy,"Sweet Dreams! " + elf_sleep,"Pleasant Dreams! " + elf_sleep,"Nighty night! " + elf_sleep,"Don't be a sleepy head! " + elf_sleepy};
	public static double gnC = 0.5;
	
	// Good morning
	 public static String [] gmT = {"good morning","just woke up","just got up from bed"};
	public static String [] gmA = {"Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!",
			"Morning sunshine!","Wakey, wakey, eggs, and bakey!","Sending you morning kisses from afar!","Morning rockstar! It’s time to start the day!","Good morning! I’ll be cheering you on from afar today.","Rise and shine, we have a day of fun ahead of us!"};
	public static double gmC = 0.5;
	
	// Bonk
	public static String [] bonkT = {"sex","seggs","pantsu","bonk me","fap","nudes","naked","boobs","bobba","oppai","pussy","penis","dick","wet dreams","spank","mommy","daddy"};
	public static String [] bonkA = {"<a:bonk_1:1154843088938876938>","<a:bonk_2:1154843091249930381>","<a:bunny_bonk:1154843092835377293>",
			"<a:chibi_bonk:1154843094622142554>","<a:chika_bonk:1154843097386188842>","<a:hutao_bonk:1154843100116684821>","<:kawaii_bonk:1154843101643407462>",
			"<a:korone_bonk:1154843103929311273>","<:neko_bonk:1154843105208574022>","<:sus_bonk:1154843106563334344>"};
	public static double bonkC = 0.2;
	
	// Mad
	public static String [] madT = {"skill issue","stupid","you bad"};
	public static String [] madA = {"<a:tof_ruby_wave:1101844569617862687>","<a:klee_rage:1086798317050150983>","<a:nyas_Madge:1120672649903874159>"};
	public static double madC = 0.2;
	
	// Fight
	public static String [] fightT = {"fight","punch","kick","shoot","die","stab","slap"};
	public static String [] fightA = {"<:cat_shoot:1154848098074497207>","<a:kawaii_shoot:1154848100544938025>","<a:kelly_punch:1154848101505454231>",
			"<a:kirby_punch:1154848103258669217>","<a:nyas_fight:1154848105016074363>","<a:nyas_fight2:1154848107197104228>","<a:nyas_kick:1154848108929355960>",
			"<a:nyas_shoot:1154848110653210755>","<:pika_punch:1154848111966032014>","<a:qiqi_shoot:1154848114084171968>" };
	public static double fightC = 0.2;
	
	// Fight
		public static String [] danceT = {"dance","cheer"};
		public static String [] danceA = {"<a:bear_dance:1156553201437380700>","<a:bear_dance_wave:1156553204021071922>","<a:gitar_dance:1156553205581365318>",
				"<a:kawaii_dance:1156553208399941714>","<a:kitten_dance:1156553209582731334>","<a:loli_dance:1156553211977670726>","<a:milk_dance:1156553213693132931>",
				"<a:peach_dance_wave:1156553216427831367>","<a:pika_dance:1156553218109747251>","<a:stitch_dance:1124669825361395752>","<a:kawa_peko_dance:1121827570808279110>"};
		public static double danceC = 0.2;
	
	// Love
	public static String [] loveT = {"love you"};
	public static String [] loveA = {elf_love,"Love you too." + elf_love};
	public static double loveC = 0.01;
	
	// Love
	public static String [] voteT = {"Vote", "Votar", "vote", "votar"};
	public static String [] voteA = {"You can vote for our server [here](https://discordservers.com/server/1204203099003158568/bump)"};
	public static double voteC = 0.01;
	
	// Chiste
	public static String [] chisteT = {"cuentame un chiste", "a ver, un chiste", "dime un chiste", "hazme reir", "chiste"};
	public static String [] chisteA = {
		"Un niño fue con su padre a ver los gatitos recién nacidos de su gata, cuando volvió le dijo a su madre: \n— Mamá han nacido dos gatitos y dos gatitas.\ny su madre pregunta:\n— ¿Cómo lo has sabido?\n— Es que papá los levantó y miró por debajo, creo que allí tienen la etiqueta.",
		"¿En donde estudian los pollitos?\n— En el Pollitécnico!",
		"¿Quién dijo «soy imbatible»?\n— Un huevo duro.",
		"¿Qué se necesita para encender una vela?\n— Que esté apagada.",
		"¿Qué le dice un semáforo a otro?\n— ¡No me mires que me estoy cambiando!",
		"¿Qué le dice una pared a otra pared?\n— Nos vemos en la esquina.",
		"¿Por qué lloraba el libro de matemáticas?\n— ¡Porque tenía muchos problemas!",
		"¿Por qué fue la computadora al doctor?\n— Porque tenía un virus.",
		"¿Qué le dice un árbol a otro árbol?\n— Nos dejaron plantados.",
		"¿Cuál animal puede saltar más alto que una casa?\n— Cualquiera, porque las casas no saltan.",
		"¿Cómo se llama el campeón de escondidas?\n— No se sabe, aún no lo han encontrado.",
		"Una madre le dice a sus hijos que están jugando en el parque:\n— Niños no jueguen en la tierra.\nEntonces los niños se fueron a jugar a Marte.",
		"¿Qué le dice un pez a otro?\n— ¡Nada!",
		"¿Tienen libros sobre el cansancio?\n— Sí, pero ahora mismo no hay, ¡están todos agotados!",
		"¿Qué le dice un gusano a otro?\n— Me voy a dar la vuelta a la manzana."
	}; //https://www.guiainfantil.com/ocio/chistes/41-chistes-malos-muy-cortos-que-van-a-hacer-reir-a-todos-los-ninos/
	public static double chisteC = 0.01;
	
	public static String[] quienT = {"Quien", "quien", "Quien\n", "quien\n"};
	public static String [] quienA = {"te pregunto"};
	public static double quienC = 1;
	
	////////////////////////////
	// Karuta TriggerCommands //
	////////////////////////////
	// Kvi
	public static String kviT = "Date Minigame";
	public static Location[] kviL = {Location.EMBED,Location.TITLE};
	
	// Kcv
	public static String kcvT = "View Clan";
	public static Location[] kcvL = {Location.EMBED,Location.TITLE};
	
	// Kcs
	public static String kcsT = "Swear Loyalty";
	public static Location[] kcsL = {Location.EMBED,Location.TITLE};
	
	// Kcb
	public static String kcbT = "Break Loyalty";
	public static Location[] kcbL = {Location.EMBED,Location.TITLE};
	
	// Kna
	public static String knaT = "Attack Node";
	public static Location[] knaL = {Location.EMBED,Location.TITLE};
	
	// Knd
	public static String kndT = "Defend Node";
	public static Location[] kndL = {Location.EMBED,Location.TITLE};
	
	// Kn
	public static String knT = "Nodes Overview";
	public static Location[] knL = {Location.EMBED,Location.TITLE};
	
	// Kn
	public static String kniT = "Node Details";
	public static Location[] kniL = {Location.EMBED,Location.TITLE};
	
	//////////////////////
	// TriggerReactions //
	//////////////////////
	// KC
	public static String kcT = "Cards owned by";
	public static Location[] kcL = {Location.EMBED,Location.DESCRIPTION};
	public static Emoji[] kcR = {Emoji.unicode("🖨️")};
	// KI
	public static String kiT = "Items carried by";
	public static Location[] kiL = {Location.EMBED,Location.DESCRIPTION};
	public static Emoji[] kiR = {Emoji.custom(Snowflake.of("1086798317050150983"), "klee_rage", true)};
	// KBI
	public static String kbiT = "Bits carried by";
	public static Location[] kbiL = {Location.EMBED,Location.DESCRIPTION};
	public static Emoji[] kbiR = {Emoji.unicode("📝")};
	
	
	
}
