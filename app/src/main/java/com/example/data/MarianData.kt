package com.example.data

import com.example.R

data class WallpaperItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val description: String,
    val drawableRes: Int,
    val tags: List<String>,
    val isPremium: Boolean = false
)

data class MarianAdvocation(
    val id: String,
    val name: String,
    val subtitle: String,
    val feastDay: String,
    val originPlace: String,
    val history: String,
    val symbolism: String,
    val maternalVirtue: String,
    val associatedDrawableRes: Int? = null
)

data class MarianPrayer(
    val id: String,
    val title: String,
    val latinTitle: String,
    val category: String,
    val text: String,
    val reflection: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

object MarianRepository {

    val wallpapers = listOf(
        WallpaperItem(
            id = "reina_cielos",
            title = "Reina de los Cielos",
            subtitle = "Corona imperial de doce estrellas y manto real de zafiro",
            category = "Exclusivos VIP",
            description = "Majestuosa obra de la Virgen María como Reina Soberana del Cielo, ceñida con diadema de doce astros luminosos y orla bordada en oro.",
            drawableRes = R.drawable.img_mary_reina_cielos,
            tags = listOf("VIP", "Reina", "Corona", "Zafiro", "Oro"),
            isPremium = true
        ),
        WallpaperItem(
            id = "inmaculada_luz",
            title = "Inmaculada de Luz Eterna",
            subtitle = "Gracia celestial derramada sobre la luna creciente",
            category = "Exclusivos VIP",
            description = "Sublime presencia de la Inmaculada en las alturas celestes, derramando rayos de amparo y consuelo maternal sobre el orbe.",
            drawableRes = R.drawable.img_mary_inmaculada_luz,
            tags = listOf("VIP", "Inmaculada", "Luna", "Rayos", "Gracia"),
            isPremium = true
        ),
        WallpaperItem(
            id = "guadalupe",
            title = "Nuestra Señora de Guadalupe",
            subtitle = "Manto celeste con cuarenta y seis estrellas doradas",
            category = "Advocaciones",
            description = "Representación celestial de la Virgen de Guadalupe, con su manto de tonalidad turquesa bordado en oro, envuelta en rayos luminosos y rodeada de rosas.",
            drawableRes = R.drawable.img_mary_guadalupe,
            tags = listOf("Guadalupe", "Estrellas", "Aura Dorada", "Rosas")
        ),
        WallpaperItem(
            id = "lourdes",
            title = "Virgen de Lourdes",
            subtitle = "La Inmaculada Concepción en la gruta de Massabielle",
            category = "Advocaciones",
            description = "La Virgen vestida de blanco níveo con ceñidor azul cielo, sosteniendo cuentas doradas y con rosas amarillas a sus pies en un entorno de paz inalterable.",
            drawableRes = R.drawable.img_mary_lourdes,
            tags = listOf("Lourdes", "Pureza", "Blanco", "Rosas")
        ),
        WallpaperItem(
            id = "vitral",
            title = "Vitral Mariano Sagrado",
            subtitle = "Arte en cristal policromado y luz celestial",
            category = "Vitrales y Arte",
            description = "Majestuoso vitral donde la figura de María resplandece en azules cobalto, ámbar cálido y destellos rubí bajo la suave luz solar.",
            drawableRes = R.drawable.img_mary_vitral,
            tags = listOf("Vitral", "Luz", "Azul Cobalto", "Arte Sacro")
        ),
        WallpaperItem(
            id = "manto_estrellas",
            title = "Manto de Doce Estrellas",
            subtitle = "Mirada maternal, lirios blancos y estrellas",
            category = "Símbolos Celestiales",
            description = "Retrato clásico de la Madre con su manto azul noche espolvoreado de estrellas refulgentes, aureola de doce astros y lirios que simbolizan pureza.",
            drawableRes = R.drawable.img_mary_manto_estrellas,
            tags = listOf("Estrellas", "Lirios", "Manto", "Serenidad")
        )
    )

    val advocations = listOf(
        MarianAdvocation(
            id = "guadalupe",
            name = "Nuestra Señora de Guadalupe",
            subtitle = "Reina y Madre maternal amorosa",
            feastDay = "12 de Diciembre",
            originPlace = "Cerro del Tepeyac, México",
            history = "Se presentó con rasgos mestizos y vestiduras celestiales, dejando grabada su imagen en una sencilla tilma de fibra de maguey que perdura intacta.",
            symbolism = "El manto turquesa tachonado de 46 estrellas en constelación exacta, la luna creciente bajo sus pies y el sol radiante que la circunda.",
            maternalVirtue = "La ternura protectora y su cercanía maternal: «¿Acaso no estoy yo aquí, que tengo el honor de ser tu Madre?»",
            associatedDrawableRes = R.drawable.img_mary_guadalupe
        ),
        MarianAdvocation(
            id = "lourdes",
            name = "Virgen de Lourdes",
            subtitle = "La fuente de pureza y consuelo",
            feastDay = "11 de Febrero",
            originPlace = "Gruta de Massabielle, Francia",
            history = "Apareció a una sencilla joven campesina en una humilde gruta rocosa, revelándose como la Inmaculada Concepción y haciendo brotar un manantial de agua pura.",
            symbolism = "Túnica y velo blanco inmaculado, ceñidor azul cielo alrededor de la cintura y dos rosas doradas que reposan sobre cada uno de sus pies descalzos.",
            maternalVirtue = "La pureza de corazón, la serenidad en la sencillez y el alivio para los que sufren congoja.",
            associatedDrawableRes = R.drawable.img_mary_lourdes
        ),
        MarianAdvocation(
            id = "fatima",
            name = "Virgen de Fátima",
            subtitle = "Señora del Rosario y Mensajera de Paz",
            feastDay = "13 de Mayo",
            originPlace = "Cova da Iria, Portugal",
            history = "Se manifestó sobre una encina vestida con un resplandor más brillante que el sol, trayendo un mensaje de súplica maternal por la paz mundial.",
            symbolism = "Vestiduras blancas ribeteadas de oro fino, manos juntas en actitud orante y un rosario blanco con cuentas brillantes.",
            maternalVirtue = "El compromiso maternal incansable por la paz en los corazones y la fraternidad entre los pueblos.",
            associatedDrawableRes = R.drawable.img_mary_vitral
        ),
        MarianAdvocation(
            id = "carmen",
            name = "Virgen del Carmen",
            subtitle = "Estrella del Mar y Madre del Carmelo",
            feastDay = "16 de Julio",
            originPlace = "Monte Carmelo",
            history = "Patrona protectora de los navegantes y marineros frente a las tempestades, portadora del escapulario como signo de consagración maternal.",
            symbolism = "Hábito carmelita marrón con capa blanca pura, y el escapulario en sus manos protectoras que ofrece a sus hijos como manto continuo.",
            maternalVirtue = "Fidelidad constante y amparo seguro en los momentos de tormenta en la vida.",
            associatedDrawableRes = R.drawable.img_mary_manto_estrellas
        ),
        MarianAdvocation(
            id = "desatanudos",
            name = "María Desatanudos",
            subtitle = "Consuelo maternal para dificultades complejas",
            feastDay = "28 de Septiembre",
            originPlace = "Augsburgo, Alemania",
            history = "Devoción nacida en el siglo XVIII a través de una pintura que representa a María desatando con infinita paciencia una cinta llena de nudos enredados.",
            symbolism = "Sus manos amorosas y atentas deshacen los nudos de una cinta, mientras sus pies pisan con firmeza la discordia para traer armonía.",
            maternalVirtue = "La paciencia infinita de una madre para desenredar los problemas y aflicciones más difíciles de sus hijos.",
            associatedDrawableRes = R.drawable.img_mary_manto_estrellas
        ),
        MarianAdvocation(
            id = "medalla_milagrosa",
            name = "Virgen de la Medalla Milagrosa",
            subtitle = "Madre que derrama torrentes de gracias",
            feastDay = "27 de Noviembre",
            originPlace = "Rue du Bac, París, Francia",
            history = "Se apareció en una capilla con anillos en sus dedos de los que brotaban rayos de luz resplandeciente hacia la tierra, pidiendo acuñar una medalla de protección.",
            symbolism = "Los rayos luminosos que emanan de sus manos abiertas simbolizan las bendiciones y consuelos maternales que colman a quienes acuden a Ella.",
            maternalVirtue = "La generosidad maternal inagotable y la disponibilidad para escuchar y abrazar a quien la busca.",
            associatedDrawableRes = R.drawable.img_mary_vitral
        ),
        MarianAdvocation(
            id = "pilar",
            name = "Virgen del Pilar",
            subtitle = "Columna inquebrantable de fortaleza",
            feastDay = "12 de Octubre",
            originPlace = "Zaragoza, España",
            history = "Tradición ancestral que narra la visita maternal de María para alentar y consolar a los desanimados, dejando un pilar de jaspe como signo perenne.",
            symbolism = "La columna o pilar sobre el cual se yergue la Madre, símbolo de estabilidad, constancia y refugio firme.",
            maternalVirtue = "La entereza de ánimo y la fortaleza moral para levantarse ante cualquier adversidad.",
            associatedDrawableRes = R.drawable.img_mary_guadalupe
        ),
        MarianAdvocation(
            id = "rosa_mistica",
            name = "Virgen Rosa Mística",
            subtitle = "Flor preciosa del jardín celestial",
            feastDay = "13 de Julio",
            originPlace = "Montichiari, Italia",
            history = "Aparición donde María se mostró con tres rosas en su pecho: una blanca, una roja y una dorada, pidiendo oración, penitencia y caridad maternal.",
            symbolism = "La rosa blanca representa la pureza del alma, la rosa roja el sacrificio amoroso y la rosa dorada la plenitud de la gracia maternal.",
            maternalVirtue = "La belleza interior, la delicadeza en el trato y el perfume de paz que esparce en cada hogar.",
            associatedDrawableRes = R.drawable.img_mary_lourdes
        )
    )

    val prayers = listOf(
        MarianPrayer(
            id = "ave_maria",
            title = "El Ave María",
            latinTitle = "Ave Maria",
            category = "Devocional",
            text = "Dios te salve, María, llena eres de gracia;\nel Señor es contigo.\nBendita tú eres entre todas las mujeres,\ny bendito es el fruto de tu vientre.\nSanta María, Madre de gracia y dulzura,\nacoge nuestras súplicas ahora\ny en la hora de nuestro descanso.\nAmén.",
            reflection = "La salutación más entrañable dirigida a María, reconociendo su plenitud de gracia y su dignidad maternal única."
        ),
        MarianPrayer(
            id = "salve_regina",
            title = "La Salve Regina",
            latinTitle = "Salve Regina",
            category = "Cántico Solemne",
            text = "Dios te salve, Reina y Madre de misericordia,\nvida, dulzura y esperanza nuestra; Dios te salve.\nA ti clamamos en este peregrinar;\na ti suspiramos, gimiendo y llorando en este valle de lágrimas.\nEa, pues, Señora, abogada nuestra,\nvuelve a nosotros esos tus ojos misericordiosos.\n¡Oh clementísima, oh piadosa, oh dulce Virgen María!\nAmén.",
            reflection = "El cántico que invoca a María como Reina y Madre de infinita compasión en momentos de cansancio y búsqueda de consuelo."
        ),
        MarianPrayer(
            id = "magnificat",
            title = "El Magnificat",
            latinTitle = "Magnificat",
            category = "Cántico de María",
            text = "Proclama mi alma la grandeza de mi ser,\ny se alegra mi espíritu en la dicha de la vida.\nPorque ha mirado la humildad de su sierva;\ndesde ahora me felicitarán todas las generaciones.\nDerriba a los soberbios de corazón\ny enaltece a los humildes.\nColma de bienes a los necesitados\ny abriga con ternura maternal a quienes la aman.\nAmén.",
            reflection = "El himno entonado por la mismísima Virgen María, ensalzando la grandeza de la humildad y la exaltación de los sencillos de corazón."
        ),
        MarianPrayer(
            id = "memorare",
            title = "Acordaos (Memorare)",
            latinTitle = "Memorare",
            category = "Súplica de San Bernardo",
            text = "Acuérdate, ¡oh piadosísima Virgen María!,\nque jamás se ha oído decir que ninguno de los que han acudido a tu protección,\nimplorando tu asistencia y reclamando tu socorro,\nhaya sido jamás desamparado por ti.\nAnimado con esta dulce confianza, a ti también acudo, ¡oh Madre, Virgen de las vírgenes!;\ny gimiendo bajo el peso de mis faltas, me postro a tus pies maternales.\n¡Oh Madre tierna!, no desoigas mis palabras,\nantes bien, escúchalas favorablemente y acógelas con amor.\nAmén.",
            reflection = "Promesa viva de que ningún hijo que acuda sinceramente al regazo de María quedará jamás sin su cobijo y respuesta maternal."
        ),
        MarianPrayer(
            id = "sub_tuum",
            title = "Bajo tu amparo",
            latinTitle = "Sub Tuum Praesidium",
            category = "Oración Milenaria",
            text = "Bajo tu amparo nos acogemos, Santa Madre y Protectora;\nno deseches las súplicas que te dirigimos en nuestras necesidades;\nantes bien, líbranos de todo peligro,\n¡oh Virgen gloriosa y bendita!\nAmén.",
            reflection = "El texto de oración mariana más antiguo conservado (siglo III), testimonio del amparo maternal ininterrumpido a lo largo de los siglos."
        ),
        MarianPrayer(
            id = "letanias",
            title = "Letanías a la Virgen María",
            latinTitle = "Litaniae Marianae",
            category = "Títulos Poéticos",
            text = "Madre purísima,\nMadre castísima,\nMadre amable,\nMadre admirable,\nMadre del buen consejo,\nVirgen prudentísima,\nVirgen digna de veneración,\nEspejo de justicia,\nTrono de la sabiduría,\nCausa de nuestra alegría,\nRosa mística,\nTorre de marfil,\nCasa de oro,\nEstrella de la mañana,\nConsuelo de los afligidos,\nReina de la paz.\nRuega y vela por nosotros con tu dulce amor.",
            reflection = "Hermosa letanía de advocaciones poéticas y espirituales que describen las incontables facetas de la belleza y ternura de María."
        ),
        MarianPrayer(
            id = "dulce_madre",
            title = "Oración a la Dulce Madre",
            latinTitle = "Dulcis Mater",
            category = "Consuelo Maternal",
            text = "Dulce Madre, no te alejes,\ntu vista de mí no apartes,\nven conmigo a todas partes\ny nunca solo me dejes.\nYa que me proteges tanto como verdadera Madre,\nhaz que me cubra tu bendición celestial\ny tu manto de estrellas sea mi refugio de paz.\nAmén.",
            reflection = "Una plegaria entrañable aprendida desde la niñez, que pide a María su compañía maternal en cada paso del camino."
        )
    )

    val conversationPrompts = listOf(
        "Madre mía, necesito tu consuelo y paz en este día",
        "Acompáñame en este momento de incertidumbre",
        "Háblame de tu pureza, humildad y serenidad",
        "¿Por qué tu manto es de color azul celeste y estrellas?",
        "Enséñame a tener paciencia y calma en el corazón",
        "Regálame un abrazo maternal y una bendición de paz",
        "¿Cómo puedo desatar los nudos de preocupación en mi vida?",
        "Cuéntame el significado de las doce estrellas de tu aureola"
    )
}
