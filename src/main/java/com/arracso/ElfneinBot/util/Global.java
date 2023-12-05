package com.arracso.ElfneinBot.util;

import com.arracso.ElfneinBot.util.Locator.Location;

import discord4j.common.util.Snowflake;
import discord4j.core.object.reaction.ReactionEmoji;

public class Global {
	
	/////////
	// IDs //
	/////////
    public static String KarutaID = "646937666251915264";
    public static String ElfneinID = "1152674294124531732";
    
    /////////////
    
    public static String loadingGIF = "https://tenor.com/view/cat-meow-loading-loading-paws-gif-5401992";
    
    /////////////////////
    // TriggerCommands //
    /////////////////////
    
    // Greatings
    public static String [] grT = {"hello", "heyo", "hi\n", "hi ", " hi"};
    public static String [] grA = {"Greatings summoner!", "Heyo heyo!", "Nice to met you!"};
    public static double grC = 0.5;
    
    // Good night
    public static String [] gnT = {"good night","going to sleep", "ima go sleep", "gonna go sleep","gotta go sleep","gotta go to sleep"};
	public static String [] gnA = {"Good Night!","Sweet Dreams!","Pleasant Dreams!","Nighty night!","Don't be a sleepy head!"};
	public static double gnC = 0.5;
	
	// Good morning
	 public static String [] gmT = {"good morning","just woke up","just got up from bed"};
	public static String [] gmA = {"Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!","Good Morning!",
			"Morning sunshine!","Wakey, wakey, eggs, and bakey!","Sending you morning kisses from afar!","Morning rockstar! It’s time to start the day!","Good morning! I’ll be cheering you on from afar today.","Rise and shine, we have a day of fun ahead of us!"};
	public static double gmC = 0.5;
	
	// Bonk
	public static String [] bonkT = {"sex","seggs","pantsu","bonk me","cum","fap","nudes","naked","boobs","bobba","oppai","pussy","penis","dick","wet dreams","spank","mommy","daddy"};
	public static String [] bonkA = {"<a:bonk_1:1154843088938876938>","<a:bonk_2:1154843091249930381>","<a:bunny_bonk:1154843092835377293>",
			"<a:chibi_bonk:1154843094622142554>","<a:chika_bonk:1154843097386188842>","<a:hutao_bonk:1154843100116684821>","<:kawaii_bonk:1154843101643407462>",
			"<a:korone_bonk:1154843103929311273>","<:neko_bonk:1154843105208574022>","<:sus_bonk:1154843106563334344>"};
	public static double bonkC = 0.5;
	
	// Mad
	public static String [] madT = {"skill issue","stupid","you bad"};
	public static String [] madA = {"<a:tof_ruby_wave:1101844569617862687>","<a:klee_rage:1086798317050150983>","<a:nyas_Madge:1120672649903874159>"};
	public static double madC = 0.2;
	
	// Fight
	public static String [] fightT = {"fight","punch","kick","shoot","gun","die","stab","slap"};
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
	public static String [] loveA = {"Love you too."};
	public static double loveC = 0.01;
	
	///////////////
	// KVI
	public static String kviT = "Date Minigame";
	public static Location[] kviL = {Location.EMBED,Location.TITLE};
	
	//////////////////////
	// TriggerReactions //
	//////////////////////
	// KC
	public static String kcT = "Cards owned by";
	public static Location[] kcL = {Location.EMBED,Location.DESCRIPTION};
	public static ReactionEmoji[] kcR = {ReactionEmoji.unicode("🖨️")};
	// KI
	public static String kiT = "Items carried by";
	public static Location[] kiL = {Location.EMBED,Location.DESCRIPTION};
	public static ReactionEmoji[] kiR = {ReactionEmoji.custom(Snowflake.of("1086798317050150983"), "klee_rage", true)};
	// KBI
	public static String kbiT = "Bits carried by";
	public static Location[] kbiL = {Location.EMBED,Location.DESCRIPTION};
	public static ReactionEmoji[] kbiR = {ReactionEmoji.unicode("📝")};
	
	
	
}
