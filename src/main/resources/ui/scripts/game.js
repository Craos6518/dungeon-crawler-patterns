/* ── Estado local JS ───────────────────────────────────────────────── */
var _selectedItemIndex = null;
var _selectedItemId = null;
var _selectedHeroType = "guerrero";
var _heroSelectionLocked = false;
var _completedThemes = [];
var _nextCampaignTheme = "poison";
var _campaignThemeOrder = ["poison", "ice", "fire", "dark"];
var _selectedLootIndex = null;
var _currentScreen = "menu";
var _dispatchQueue = [];
var _dispatchLocked = false;
var _loreQueue = [];
var _loreVisible = false;
var _pendingRunStartLore = null;
var _returnToMainMenuAfterLore = false;
var _loreState = {
  worldShown: false,
  heroStoryShown: false,
  guardianShown: {},
  endingShown: {},
  campaignFinalShown: false,
  creditsShown: false,
  previousFrame: null,
};

var LORE_THEME_BOOK = {
  fire: {
    dungeonName: "El Volcan de Ignareth",
    dungeonIntroTitle: "Tercera Mazmorra",
    dungeonIntroSubtitle: "Bajo Ignareth arde un fuego que consume memorias",
    dungeonIntroLines: [
      "Bajo la montana de Ignareth arde un fuego que no consume madera sino memorias.",
      "Pyraxis, Salamandra Ancestral, guarda la Piedra del Fuego Eterno desde que el mundo era joven.",
      "No es solo un monstruo: es la guardiana legitima del artefacto, maldita a su rol eterno.",
      "Pasillos de obsidiana negra y rios de lava forjan un ciclo de fuego y renacimiento.",
    ],
    guardianName: "Pyraxis",
    guardianTitle: "Guardian de Ignareth · Salamandra Ancestral",
    guardianLines: [
      "VIDA 225 · ATAQUE 29 · DEFENSA 36.",
      "Antes de ser guardiana, Pyraxis fue un ser libre.",
      "La maldicion de Thessanor la encadeno a la Piedra del Fuego Eterno: mientras el artefacto exista, ella no puede morir, pero tampoco puede abandonar las cavernas.",
      "Cada aventurero que derrota reaviva en ella la esperanza de que alguno sea lo suficientemente fuerte como para liberarla.",
    ],
    endingTitle: "El Corazon del Volcan se ha Apagado",
    endingSubtitle: "Final de Volcan de Ignareth",
    endingLines: [
      "Al derrotar a Pyraxis, la lava se enfria y el volcan entra en un largo letargo.",
      "La tierra comienza a sanar y el continente evita una erupcion catastrofica.",
      "Desbloqueo: Armadura de Obsidiana.",
    ],
  },
  ice: {
    dungeonName: "Las Catacumbas de Glaciurvh",
    dungeonIntroTitle: "Segunda Mazmorra",
    dungeonIntroSubtitle: "Bajo la llanura helada duerme el invierno sin fin",
    dungeonIntroLines: [
      "Al norte del continente, donde el sol no toca la tierra, yace la Llanura del Silencio Helado.",
      "Bajo ella, las catacumbas guardan el Cristal del Hielo Primordial.",
      "Kryovaleth, Dragon de Invierno, fue formado por siglos de nieve acumulada y dolor congelado.",
      "Aqui el hielo no solo congela el cuerpo: ralentiza pensamientos y adormece la voluntad.",
    ],
    guardianName: "Kryovaleth",
    guardianTitle: "Espiritu del Invierno · Dragon de Hielo",
    guardianLines: [
      "VIDA 190 · ATAQUE 25 · DEFENSA 32.",
      "No recuerda haber sido creado: simplemente existe, como el frio.",
      "Sus estrategias de combate son adaptativas; comienza defensivo, evaluando al rival, y solo ataca cuando la victoria esta garantizada al 97%.",
      "Es la personificacion del patron Strategy en su forma mas pura.",
    ],
    endingTitle: "El Invierno Eterno se Derrite",
    endingSubtitle: "Final de Catacumbas de Glaciurvh",
    endingLines: [
      "Kryovaleth se hace anicos, y con el, su hechizo de invierno sin fin.",
      "Los glaciares retroceden y la primavera regresa a las tierras congeladas.",
      "Desbloqueo: Espada Glacial.",
    ],
  },
  poison: {
    dungeonName: "Los Pantanos de Viridax",
    dungeonIntroTitle: "Primera Mazmorra",
    dungeonIntroSubtitle: "Vida retorcida, veneno y degeneracion lenta",
    dungeonIntroLines: [
      "En Viridax, la vida se pudre en patrones hermosos y peligrosos.",
      "La Semilla de la Vida Corrupta creo un ecosistema de veneno y degradacion.",
      "Arachnovex, la Reina Tejedora, ha consumido tantos aventureros que sus ojos reflejan almas atrapadas.",
      "El veneno no mata de golpe: corrompe capa por capa, como un decorador no deseado sobre una base sana.",
    ],
    guardianName: "Arachnovex",
    guardianTitle: "Reina Tejedora · Arana Primigenia",
    guardianLines: [
      "VIDA 150 · ATAQUE 21 · DEFENSA 18.",
      "Arachnovex teje trampas con la misma precision con que un programador disena una trampa en el codigo.",
      "Sus ataques no son directos: aplican veneno, quemadura y entumecimiento en capas sucesivas, decoradores apilados uno sobre otro.",
      "La presa colapsa sin entender que la mato.",
    ],
    endingTitle: "El Pantano Purificado",
    endingSubtitle: "Final de Pantanos de Viridax",
    endingLines: [
      "Con Arachnovex destruido, el veneno que corrompia el pantano se neutraliza.",
      "Las aguas se vuelven cristalinas y flora y fauna recuperan su forma natural.",
      "Desbloqueo: Arco de Tejo Toxico.",
    ],
  },
  dark: {
    dungeonName: "La Ciudadela de Umbrakar",
    dungeonIntroTitle: "Cuarta Mazmorra",
    dungeonIntroSubtitle: "La encarnacion del olvido y la memoria fragmentada",
    dungeonIntroLines: [
      "La Ciudadela de Umbrakar surgio cuando el Fragmento de la Oscuridad Absoluta cayo del cielo.",
      "Sus pasillos cambian, sus salas se reorganizan y todo intento de orientacion colapsa.",
      "Malachar no es un ser vivo: es la abstraccion del olvido.",
      "Sin un memento solido, quienes entran son sobrescritos y regresan sin identidad.",
    ],
    guardianName: "Malachar",
    guardianTitle: "El Sin-Nombre · Senor del Vacio",
    guardianLines: [
      "VIDA 290 · ATAQUE 33 · DEFENSA 40.",
      "Malachar no ataca el cuerpo: ataca el estado.",
      "Su habilidad mas temida, Borrar Memoria, revierte al heroe a su estado anterior sin guardar, como un rollback de Memento.",
      "Derrotarlo requiere no solo fuerza, sino un sistema de persistencia impecable.",
    ],
    endingTitle: "La Sombra se Disipa",
    endingSubtitle: "Final de Ciudadela de Umbrakar",
    endingLines: [
      "La caida de Malachar libera a la ciudadela de su control sombrio.",
      "La luz del sol vuelve a entrar y los espiritus atrapados son liberados.",
      "Desbloqueo: Daga de las Sombras.",
    ],
  },
};

var HERO_LORE_BOOK = {
  guerrero: {
    title: "Kael Ferrum",
    subtitle: "El Ultimo Guardian · Guerrero",
    lines: [
      "Vida 110 · Ataque 20 · Defensa 26 · Veloc 11.",
      "Hijo de un herrero del sur de Valdrath, Kael perdio a su familia cuando Ignareth envio sus primeras oleadas de criaturas a la superficie.",
      "No busca gloria ni riquezas: solo quiere que ninguna familia mas pague su precio.",
      "Lleva tatuado en el antebrazo izquierdo el simbolo de su aldea, un recuerdo que ninguna sombra puede borrarle.",
    ],
    theme: "fire",
  },
  mago: {
    title: "Sylara Vex",
    subtitle: "Archivista del Vacio · Maga",
    lines: [
      "Vida 65 · Ataque 27 · Defensa 10 · Veloc 23.",
      "Sylara estudio en la Academia de Valdrath hasta hallar registros prohibidos: los planos originales del Archimago Thessanor.",
      "Comprendio que las mazmorras no son caos: son sistemas disenados, como codigo que pocos pueden leer.",
      "Entrar en ellas es leer arquitectura arcana ajena, y ella domina el arte de descifrar patrones invisibles para otros.",
    ],
    theme: "ice",
  },
  arquero: {
    title: "Thoran Silvis",
    subtitle: "Explorador de la Bruma · Arquero",
    lines: [
      "Vida 85 · Ataque 23 · Defensa 17 · Veloc 21.",
      "Criado entre los bosques de Mirval, Thoran aprendio que la distancia entre cazador y presa es sagrada.",
      "Cuando los pantanos de Viridax se expandieron hacia el norte, su hogar desaparecio bajo la maleza venenosa en semanas.",
      "Ahora viaja ligero, con el recuerdo del bosque vivo guardado en cada flecha.",
    ],
    theme: "poison",
  },
};

var HERO_WORLD_ENDING_BOOK = {
  guerrero: {
    title: "Kael Ferrum, Escudo de Eranthia",
    subtitle: "Final del Guerrero",
    lines: [
      "Kael regresa a Valdrath y funda la Guardia Ferrum para proteger los cuatro sellos restaurados.",
      "Convierte las antiguas rutas de guerra en caminos seguros para caravanas y aldeas.",
      "Su juramento final queda grabado en piedra: ningun nino perdera su hogar por culpa de las mazmorras.",
    ],
    theme: "fire",
  },
  mago: {
    title: "Sylara Vex, Cronista de los Cuatro Sellos",
    subtitle: "Final de la Maga",
    lines: [
      "Sylara recompone los fragmentos del codex de Thessanor y redacta una nueva doctrina para la magia etica.",
      "El Consejo de Valdrath la nombra Archivista Suprema y custodio oficial de los artefactos purificados.",
      "Su legado inaugura una era donde el conocimiento arcano sirve a la vida y no al dominio.",
    ],
    theme: "ice",
  },
  arquero: {
    title: "Thoran Silvis, Centinela del Nuevo Bosque",
    subtitle: "Final del Arquero",
    lines: [
      "Thoran lidera la reforestacion de Mirval y transforma Viridax en un santuario vivo.",
      "Las antiguas zonas de caza se convierten en corredores protegidos para viajeros y criaturas.",
      "Desde su atalaya, vigila el equilibrio del continente con la misma precision de cada flecha.",
    ],
    theme: "poison",
  },
};

var ART_ASSETS = {
  heroes: {
    guerrero: "assets/images/heroes/kael-ferrum.png",
    mago: "assets/images/heroes/sylara-vex.png",
    arquero: "assets/images/heroes/thoran-silvis.png",
  },
  corridors: {
    fire: "assets/images/corridors/fire.png",
    ice: "assets/images/corridors/ice.png",
    poison: "assets/images/corridors/poison.png",
    dark: "assets/images/corridors/dark.png",
  },
  sentinelsByTheme: {
    fire: "assets/images/centinelas/Salamandra de Fuego.png",
    ice: "assets/images/centinelas/Lobo de Hielo.png",
    poison: "assets/images/centinelas/Araña Venenosa.png",
    dark: "assets/images/centinelas/Sombra Errante.png",
  },
  sentinelsByName: {
    "salamandra de fuego": "assets/images/centinelas/Salamandra de Fuego.png",
    "lobo de hielo": "assets/images/centinelas/Lobo de Hielo.png",
    "arana venenosa": "assets/images/centinelas/Araña Venenosa.png",
    "sombra errante": "assets/images/centinelas/Sombra Errante.png",
  },
  minibossesByTheme: {
    fire: "assets/images/semijefes/Orco Flamígero.png",
    ice: "assets/images/semijefes/Orco Glacial.png",
    poison: "assets/images/semijefes/Orco Putrefacto.png",
    dark: "assets/images/semijefes/Caballero Oscuro.png",
  },
  minibossesByName: {
    "orco flamigero": "assets/images/semijefes/Orco Flamígero.png",
    "orco glacial": "assets/images/semijefes/Orco Glacial.png",
    "orco putrefacto": "assets/images/semijefes/Orco Putrefacto.png",
    "caballero oscuro": "assets/images/semijefes/Caballero Oscuro.png",
  },
  bossesByTheme: {
    fire: "assets/images/bosses/pyraxis.png",
    ice: "assets/images/bosses/kryovaleth.png",
    poison: "assets/images/bosses/arachnovex.jpg",
    dark: "assets/images/bosses/malachar.png",
  },
};

function normalizeHeroType(heroType) {
  var normalized = String(heroType || "")
    .trim()
    .toLowerCase();
  if (
    normalized === "guerrero" ||
    normalized === "mago" ||
    normalized === "arquero"
  ) {
    return normalized;
  }
  return "guerrero";
}

function resolveHeroLore(heroType) {
  return HERO_LORE_BOOK[normalizeHeroType(heroType)] || HERO_LORE_BOOK.guerrero;
}

function resolveHeroWorldEnding(heroType) {
  return (
    HERO_WORLD_ENDING_BOOK[normalizeHeroType(heroType)] ||
    HERO_WORLD_ENDING_BOOK.guerrero
  );
}

function normalizeThemeKey(themeKey) {
  var normalized = String(themeKey || "")
    .trim()
    .toLowerCase();
  if (
    normalized === "fire" ||
    normalized === "ice" ||
    normalized === "poison" ||
    normalized === "dark"
  ) {
    return normalized;
  }
  return "poison";
}

function resolveThemeLore(themeKey) {
  return LORE_THEME_BOOK[normalizeThemeKey(themeKey)] || LORE_THEME_BOOK.poison;
}

function normalizeSearchToken(value) {
  return String(value || "")
    .trim()
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");
}

function buildAssetUrl(assetPath) {
  return encodeURI(String(assetPath || "").trim());
}

function applyBackgroundAsset(el, assetPath, loadedClassName) {
  if (!el) return;

  var normalizedPath = String(assetPath || "").trim();
  var assetUrl = buildAssetUrl(normalizedPath);
  el.dataset.pendingAsset = assetUrl;

  if (!assetUrl) {
    el.style.backgroundImage = "";
    el.classList.remove(loadedClassName);
    return;
  }

  var probe = new Image();
  probe.onload = function () {
    if (el.dataset.pendingAsset !== assetUrl) return;
    el.style.backgroundImage = 'url("' + assetUrl + '")';
    el.classList.add(loadedClassName);
    console.log("[✓] Image loaded: " + assetUrl);
  };
  probe.onerror = function () {
    if (el.dataset.pendingAsset !== assetUrl) return;
    el.style.backgroundImage = "";
    el.classList.remove(loadedClassName);
    console.error("[✗] Image failed to load: " + assetUrl);
  };
  console.log("[→] Loading image: " + assetUrl);
  probe.src = assetUrl;
}

function applyIllustrationAsset(elementId, assetPath) {
  var el = document.getElementById(elementId);
  applyBackgroundAsset(el, assetPath, "placeholder-img--has-art");
}

function applyHeroPortraitAsset(heroType, assetPath) {
  var portrait = document.getElementById(
    "hero-portrait-" + normalizeHeroType(heroType),
  );
  applyBackgroundAsset(portrait, assetPath, "hero-card__portrait--has-art");
}

function resolveBossAsset(enemyName, themeKey) {
  var name = normalizeSearchToken(enemyName);
  if (name.indexOf("pyraxis") >= 0) return ART_ASSETS.bossesByTheme.fire;
  if (name.indexOf("kryovaleth") >= 0) return ART_ASSETS.bossesByTheme.ice;
  if (name.indexOf("arachnovex") >= 0) return ART_ASSETS.bossesByTheme.poison;
  if (name.indexOf("malachar") >= 0) return ART_ASSETS.bossesByTheme.dark;
  return ART_ASSETS.bossesByTheme[normalizeThemeKey(themeKey)] || "";
}

function normalizeEnemyTier(tierValue) {
  var tier = normalizeSearchToken(tierValue);
  if (tier === "jefe" || tier === "boss") return "jefe";
  if (
    tier === "semi-jefe" ||
    tier === "semijefe" ||
    tier === "semi jefe" ||
    tier === "elite"
  )
    return "semi-jefe";
  if (tier === "centinela" || tier === "menor") return "centinela";
  return "";
}

function formatEnemyTierLabel(tierValue) {
  var normalizedTier = normalizeEnemyTier(tierValue);
  if (normalizedTier === "jefe") return "Jefe";
  if (normalizedTier === "semi-jefe") return "Semi-jefe";
  if (normalizedTier === "centinela") return "Centinela";
  return String(tierValue || "");
}

function resolveEnemyAsset(enemy, themeKey, fallbackAsset) {
  if (!enemy) return fallbackAsset;

  var tier = normalizeEnemyTier(enemy.tier);
  var theme = normalizeThemeKey(themeKey);
  var enemyName = normalizeSearchToken(enemy.name);

  console.log(
    "[Enemy Asset Resolution] Name: " +
      enemy.name +
      " | Tier: " +
      enemy.tier +
      " → " +
      tier +
      " | Theme: " +
      theme +
      " | Normalized name: " +
      enemyName,
  );

  if (tier === "jefe") {
    var bossAsset = resolveBossAsset(enemy.name, theme) || fallbackAsset;
    console.log("  → Boss asset: " + bossAsset);
    return bossAsset;
  }
  if (tier === "semi-jefe") {
    var minibossAsset =
      ART_ASSETS.minibossesByName[enemyName] ||
      ART_ASSETS.minibossesByTheme[theme] ||
      fallbackAsset;
    console.log(
      "  → Mini-boss asset (by name/theme/fallback): " + minibossAsset,
    );
    return minibossAsset;
  }
  if (tier === "centinela") {
    var sentinelAsset =
      ART_ASSETS.sentinelsByName[enemyName] ||
      ART_ASSETS.sentinelsByTheme[theme] ||
      fallbackAsset;
    console.log(
      "  → Sentinel asset (by name/theme/fallback): " + sentinelAsset,
    );
    return sentinelAsset;
  }
  console.log("  → Unknown tier, using fallback: " + fallbackAsset);
  return fallbackAsset;
}

function renderHeroPortraitAssets() {
  applyHeroPortraitAsset("guerrero", ART_ASSETS.heroes.guerrero);
  applyHeroPortraitAsset("mago", ART_ASSETS.heroes.mago);
  applyHeroPortraitAsset("arquero", ART_ASSETS.heroes.arquero);
}

function renderSceneAssets(state) {
  var theme = normalizeThemeKey(state && state.theme ? state.theme : "poison");
  var corridorAsset =
    ART_ASSETS.corridors[theme] || ART_ASSETS.corridors.poison;
  applyIllustrationAsset("s1-room-illustration", corridorAsset);

  var duelAsset = corridorAsset;
  var enemy = state && state.enemy ? state.enemy : null;
  duelAsset = resolveEnemyAsset(enemy, theme, duelAsset);
  applyIllustrationAsset("s2-duel-illustration", duelAsset);
}

function formatHeroTypeLabel(heroType) {
  var normalized = normalizeHeroType(heroType);
  if (normalized === "mago") return "Mago";
  if (normalized === "arquero") return "Arquero";
  return "Guerrero";
}

function buildHeroLoreEntry(heroType) {
  var lore = resolveHeroLore(heroType);
  return {
    chapter: "CRONICAS DEL HEROE",
    title: lore.title,
    subtitle: lore.subtitle,
    lines: lore.lines,
    theme: lore.theme,
  };
}

function buildWorldLoreEntry() {
  return {
    chapter: "CRONICAS DE ERANTHIA",
    title: "El Mundo de Eranthia",
    subtitle: "Los Albores del Tiempo",
    lines: [
      "En los albores del tiempo, cuando los dioses caminaban entre mortales, el Archimago Valdur Thessanor forjo cuatro artefactos de poder inconmensurable.",
      "La Piedra del Fuego Eterno, el Cristal del Hielo Primordial, la Semilla de la Vida Corrupta y el Fragmento de la Oscuridad Absoluta fueron sellados en el corazon de cuatro mazmorras.",
      "Cinco siglos despues, el equilibrio se rompe: los sellos se debilitan y las criaturas de las profundidades despiertan hambrientas.",
      "El Consejo de Magos de Valdrath emite una ultima llamada: se necesita un heroe de codigo noble para restaurar el mundo.",
      "Las mazmorras obedecen patrones ancestrales; quien entiende sus reglas anticipa el peligro, quien las ignora perece en el caos.",
    ],
    theme: "fire",
  };
}

function buildDungeonLoreEntry(themeKey) {
  var lore = resolveThemeLore(themeKey);
  return {
    chapter: "CRONICAS DE MAZMORRA",
    title: lore.dungeonIntroTitle,
    subtitle: lore.dungeonName + " · " + lore.dungeonIntroSubtitle,
    lines: lore.dungeonIntroLines,
    theme: normalizeThemeKey(themeKey),
  };
}

function buildGuardianLoreEntry(themeKey) {
  var lore = resolveThemeLore(themeKey);
  return {
    chapter: "CRONICAS DEL GUARDIAN",
    title: lore.guardianName,
    subtitle: lore.guardianTitle,
    lines: lore.guardianLines,
    theme: normalizeThemeKey(themeKey),
  };
}

function buildEndingLoreEntry(themeKey) {
  var lore = resolveThemeLore(themeKey);
  return {
    chapter: "EPILOGO DE MAZMORRA",
    title: lore.endingTitle,
    subtitle: lore.endingSubtitle,
    lines: lore.endingLines,
    theme: normalizeThemeKey(themeKey),
  };
}

function buildCampaignFinalLoreEntry(heroType) {
  var ending = resolveHeroWorldEnding(heroType);
  return {
    chapter: "EPILOGO DEL MUNDO",
    title: "Eranthia ha sido Restaurada",
    subtitle: ending.subtitle + " · Campana Completa",
    lines: [
      "Los cuatro artefactos han sido purificados y el equilibrio elemental vuelve al continente.",
      "Fuego, hielo, vida y sombra dejan de luchar entre si: Eranthia respira en paz por primera vez en siglos.",
      "Las mazmorras permanecen selladas bajo una nueva custodia, no por maldicion, sino por decision del heroe.",
      "Destino final: " + ending.title + ".",
      ending.lines[0],
      ending.lines[1],
      ending.lines[2],
    ],
    theme: normalizeThemeKey(ending.theme),
  };
}

function buildCreditsLoreEntry() {
  return {
    chapter: "CREDITOS",
    title: "Dungeon Crawler - Patrones de Diseno",
    subtitle: "Gracias por completar las 4 mazmorras",
    lines: [
      "Desarrollador: Andres Felipe Martinez Henao - Craos6518.",
      "Entorno de desarrollo: Visual Studio Code 1.114.0, Maven, JavaFX WebView y Git.",
      "OS y hardware: Fedora Linux 43 KDE Plasma, ASUS VivoBook X412DA, AMD Ryzen 5 3500U, Radeon Vega 8, RAM 5.72 GiB.",
      "Java 17 runtime: Temurin OpenJDK 17.0.18+8.",
      "Agentes de IA: GPT-5.3-Codex - Xhigh, Claude Opus 4.6 y Claude Sonnet 4.6.",
      "Docente: Dinora Seneth Monsalve.",
      "Materia: Patrones de diseno.",
      "Analytics: Telemetria de eventos de sesion con Observer, contadores y trazas de comandos UI.",
    ],
    theme: "dark",
  };
}

function countCompletedCampaignThemes(completedThemes) {
  if (!Array.isArray(completedThemes) || completedThemes.length === 0) {
    return 0;
  }

  var unique = {};
  completedThemes.forEach(function (theme) {
    var normalized = normalizeThemeKey(theme);
    if (normalized) {
      unique[normalized] = true;
    }
  });

  return Object.keys(unique).length;
}

function renderLoreBody(lines) {
  var body = document.getElementById("lore-window-body");
  if (!body) return;
  body.innerHTML = "";
  (Array.isArray(lines) ? lines : []).forEach(function (line) {
    var p = document.createElement("p");
    p.className = "lore-window__line";
    p.textContent = String(line || "");
    body.appendChild(p);
  });
}

function showLoreWindow(entry) {
  var overlay = document.getElementById("lore-window-overlay");
  if (!overlay || !entry) return;

  setNodeText("lore-window-eyebrow", entry.chapter || "CRONICAS");
  setNodeText("lore-window-title", entry.title || "Relato");
  setNodeText("lore-window-subtitle", entry.subtitle || "");
  renderLoreBody(entry.lines || []);

  overlay.dataset.theme = normalizeThemeKey(entry.theme || "poison");
  overlay.classList.add("lore-overlay--visible");
  overlay.setAttribute("aria-hidden", "false");
  document.body.classList.add("lore-open");
  _loreVisible = true;
}

function showNextLoreWindow() {
  if (_loreVisible || _loreQueue.length === 0) return;
  showLoreWindow(_loreQueue.shift());
}

function enqueueLoreWindow(entry) {
  if (!entry) return;
  _loreQueue.push(entry);
  showNextLoreWindow();
}

function dismissLoreWindow() {
  var overlay = document.getElementById("lore-window-overlay");
  if (!overlay) return;
  overlay.classList.remove("lore-overlay--visible");
  overlay.setAttribute("aria-hidden", "true");
  document.body.classList.remove("lore-open");
  _loreVisible = false;
  showNextLoreWindow();

  if (!_loreVisible && _loreQueue.length === 0 && _returnToMainMenuAfterLore) {
    _returnToMainMenuAfterLore = false;
    if (_currentScreen !== "menu") {
      sendCommand("openMainMenu", {});
    }
  }
}

function resetCampaignNarrativeState() {
  _loreState.worldShown = false;
  _loreState.heroStoryShown = false;
  _loreState.guardianShown = {};
  _loreState.endingShown = {};
  _loreState.campaignFinalShown = false;
  _loreState.creditsShown = false;
  _loreState.previousFrame = null;
  _returnToMainMenuAfterLore = false;
}

function startNewCampaignNarrativeFlow() {
  resetCampaignNarrativeState();
  _loreState.worldShown = true;
  enqueueLoreWindow(buildWorldLoreEntry());
}

function queueHeroAndDungeonLoreForSelection(themeKey) {
  var normalizedTheme = normalizeThemeKey(themeKey);

  if (!_heroSelectionLocked && !_loreState.heroStoryShown) {
    _loreState.heroStoryShown = true;
    enqueueLoreWindow(buildHeroLoreEntry(_selectedHeroType));
  }

  enqueueLoreWindow(buildDungeonLoreEntry(normalizedTheme));
}

function openWorldLoreWindow() {
  _loreState.worldShown = true;
  enqueueLoreWindow(buildWorldLoreEntry());
}

function evaluateLoreTriggers(state) {
  var currentTheme = normalizeThemeKey(state.theme || "poison");
  var completedCount = countCompletedCampaignThemes(state.completedThemes);
  var currentHeroType = normalizeHeroType(
    state.heroType || _selectedHeroType || "guerrero",
  );

  var currentFrame = {
    screen: String(state.screen || ""),
    theme: currentTheme,
    room: Number(state.room || 0),
    totalRooms: Number(state.totalRooms || 0),
    roomHasEnemy: !!state.roomHasEnemy,
    enemyTier: normalizeEnemyTier(
      state.enemy && state.enemy.tier ? state.enemy.tier : "",
    ),
    completedCount: completedCount,
    heroType: currentHeroType,
  };

  if (
    currentFrame.screen === "combat" &&
    currentFrame.enemyTier === "jefe" &&
    !_loreState.guardianShown[currentTheme]
  ) {
    _loreState.guardianShown[currentTheme] = true;
    enqueueLoreWindow(buildGuardianLoreEntry(currentTheme));
  }

  var previous = _loreState.previousFrame;
  var bossJustDefeated =
    previous &&
    previous.screen === "combat" &&
    previous.enemyTier === "jefe" &&
    currentFrame.screen === "exploration" &&
    currentFrame.totalRooms > 0 &&
    currentFrame.room === currentFrame.totalRooms &&
    !currentFrame.roomHasEnemy;

  var dungeonJustCompleted =
    previous &&
    previous.screen === "treasure" &&
    (currentFrame.screen === "hero" || currentFrame.screen === "menu") &&
    currentFrame.completedCount > previous.completedCount;

  if (
    (bossJustDefeated || dungeonJustCompleted) &&
    !_loreState.endingShown[currentTheme]
  ) {
    _loreState.endingShown[currentTheme] = true;
    enqueueLoreWindow(buildEndingLoreEntry(currentTheme));
  }

  if (
    dungeonJustCompleted &&
    currentFrame.completedCount >= 4 &&
    !_loreState.campaignFinalShown
  ) {
    _loreState.campaignFinalShown = true;
    enqueueLoreWindow(buildCampaignFinalLoreEntry(currentFrame.heroType));
  }

  if (_loreState.campaignFinalShown && !_loreState.creditsShown) {
    _loreState.creditsShown = true;
    _returnToMainMenuAfterLore = true;
    enqueueLoreWindow(buildCreditsLoreEntry());
  }

  _loreState.previousFrame = currentFrame;
}

function setNodeText(id, val) {
  var el = document.getElementById(id);
  if (el) el.textContent = val !== null && val !== undefined ? String(val) : "";
}

function formatResourceLabel(resourceType) {
  var normalized = String(resourceType || "")
    .trim()
    .toLowerCase();
  if (normalized === "mana") return "Mana";
  if (normalized === "concentracion") return "Concentracion";
  return "Stamina";
}

function showTransientSystemMessage(message) {
  if (!message) return;

  var targets = [
    document.getElementById("s2-combat-log"),
    document.getElementById("s1-event-log"),
  ];

  for (var i = 0; i < targets.length; i++) {
    var target = targets[i];
    if (!target) continue;

    var p = document.createElement("p");
    p.className = "log-entry";
    p.textContent = "[SYS] " + message;
    target.appendChild(p);
    return;
  }

  showUiToast(message);
}

function showUiToast(message) {
  if (!message) return;

  var toast = document.getElementById("ui-system-toast");
  if (!toast) {
    toast = document.createElement("div");
    toast.id = "ui-system-toast";
    toast.style.position = "fixed";
    toast.style.top = "12px";
    toast.style.left = "50%";
    toast.style.transform = "translateX(-50%)";
    toast.style.zIndex = "12000";
    toast.style.maxWidth = "min(90vw, 760px)";
    toast.style.padding = "10px 14px";
    toast.style.border = "1px solid #8f2626";
    toast.style.background = "rgba(22, 8, 8, 0.95)";
    toast.style.color = "#f8d3d3";
    toast.style.fontFamily = "'VT323', monospace";
    toast.style.fontSize = "24px";
    toast.style.letterSpacing = ".03em";
    toast.style.boxShadow = "3px 3px 0 #000";
    toast.style.display = "none";
    document.body.appendChild(toast);
  }

  toast.textContent = String(message);
  toast.style.display = "block";

  if (showUiToast._timer) {
    clearTimeout(showUiToast._timer);
  }
  showUiToast._timer = setTimeout(function () {
    toast.style.display = "none";
  }, 3600);
}

function applyBridgeResponse(rawResponse) {
  if (!rawResponse) return;

  var parsed;
  try {
    parsed =
      typeof rawResponse === "string" ? JSON.parse(rawResponse) : rawResponse;
  } catch (err) {
    console.warn("Respuesta backend invalida:", rawResponse);
    return;
  }

  if (parsed && parsed.data) {
    window.updateGameState(parsed.data);
  }

  if (parsed && parsed.status === "error" && parsed.message) {
    _pendingRunStartLore = null;
    showTransientSystemMessage(parsed.message);
  }
}

function dispatchNow(command) {
  var serialized = JSON.stringify(command);

  if (window.javabridge && typeof window.javabridge.dispatch === "function") {
    try {
      var response = window.javabridge.dispatch(serialized);
      applyBridgeResponse(response);
      return;
    } catch (err) {
      console.warn("Fallo dispatch directo; usando fallback alert.", err);
    }
  }

  // Fallback de compatibilidad para puentes antiguos.
  window.alert(serialized);
}

function flushDispatchQueue() {
  if (_dispatchLocked) return;
  _dispatchLocked = true;

  while (_dispatchQueue.length > 0) {
    var command = _dispatchQueue.shift();
    dispatchNow(command);
  }

  _dispatchLocked = false;
}

/* ── sendCommand: puente robusto JS -> JavaFX ── */
function sendCommand(action, payload) {
  _dispatchQueue.push({ action: action, payload: payload || {} });
  flushDispatchQueue();
}

function setCombatTacticsMenuOpen(open) {
  var shell = document.getElementById("combat-tactics-shell");
  var menu = document.getElementById("combat-tactics-menu");
  var trigger = document.getElementById("btn-combat-tactics");
  if (!shell || !menu || !trigger) return;

  var isOpen = !!open;
  menu.hidden = !isOpen;
  trigger.setAttribute("aria-expanded", isOpen ? "true" : "false");
  shell.classList.toggle("combat-tactics--open", isOpen);
}

function closeCombatTacticsMenu() {
  setCombatTacticsMenuOpen(false);
}

function toggleCombatTacticsMenu() {
  var menu = document.getElementById("combat-tactics-menu");
  if (!menu) return;
  setCombatTacticsMenuOpen(menu.hidden);
}

/* ── Selección de héroe ── */
function selectHeroCard(heroType) {
  _selectedHeroType = heroType;
  document.querySelectorAll(".hero-card").forEach(function (card) {
    var sel = card.dataset.hero === heroType;
    card.classList.toggle("hero-card--selected", sel);
    var badge = card.querySelector(".hero-selected-badge");
    if (!badge) {
      badge = document.createElement("div");
      badge.className = "hero-selected-badge";
      badge.textContent = "SELECCIONADO";
      card.appendChild(badge);
    }
    badge.style.display = sel ? "block" : "none";
  });
}

function isThemeCompleted(themeKey) {
  if (!Array.isArray(_completedThemes)) return false;
  return _completedThemes.indexOf(String(themeKey || "").toLowerCase()) >= 0;
}

function isThemeLockedByOrder(themeKey) {
  var normalizedTheme = String(themeKey || "").toLowerCase();
  if (!_nextCampaignTheme) {
    return true;
  }
  return normalizedTheme !== String(_nextCampaignTheme).toLowerCase();
}

function applyHeroScreenRestrictions() {
  document.querySelectorAll(".hero-card").forEach(function (card) {
    var cardHero = (card.dataset.hero || "").toLowerCase();
    var isDisabled =
      _heroSelectionLocked &&
      cardHero !== String(_selectedHeroType || "").toLowerCase();
    card.classList.toggle("hero-card--disabled", isDisabled);
    card.dataset.state = isDisabled ? "disabled" : "default";
    card.setAttribute("aria-disabled", isDisabled ? "true" : "false");
  });

  document.querySelectorAll(".theme-card").forEach(function (card) {
    var themeKey = (card.dataset.theme || "").toLowerCase();
    var alreadyCompleted = isThemeCompleted(themeKey);
    var lockedByOrder = isThemeLockedByOrder(themeKey);
    var isDisabled = alreadyCompleted || lockedByOrder;
    card.classList.toggle("theme-card--disabled", isDisabled);
    card.dataset.state = isDisabled ? "disabled" : "default";
    card.setAttribute("aria-disabled", isDisabled ? "true" : "false");
    if (alreadyCompleted) {
      card.title = "Mazmorra ya completada";
    } else if (lockedByOrder && _nextCampaignTheme) {
      card.title = "Primero debes completar: " + String(_nextCampaignTheme);
    } else if (lockedByOrder) {
      card.title = "Campaña completada";
    } else {
      card.title = "";
    }
  });
}

/* ── Selección de slot de guardado ── */
function selectSaveSlotRow(slot) {
  document.querySelectorAll(".save-slot-row").forEach(function (row) {
    row.classList.toggle(
      "save-slot-row--active",
      Number(row.dataset.slot) === slot,
    );
  });
}

/* ── Selección de item en inventario ── */
function selectInventoryRow(el) {
  document.querySelectorAll("#s3-item-rows .item-row").forEach(function (row) {
    row.classList.remove("item-row--selected");
    row.setAttribute("aria-selected", "false");
  });
  el.classList.add("item-row--selected");
  el.setAttribute("aria-selected", "true");
  _selectedItemIndex = Number(el.dataset.itemIndex);
  _selectedItemId = el.dataset.itemId;
}

/* ── Render dinámico del inventario ── */
function renderInventoryItems(items, selectedIndex) {
  var rowsContainer = document.getElementById("s3-item-rows");
  if (!rowsContainer) return;
  rowsContainer.innerHTML = "";
  if (!Array.isArray(items) || items.length === 0) {
    var empty = document.createElement("p");
    empty.className = "log-entry log-entry--placeholder";
    empty.textContent = "Inventario vacio";
    rowsContainer.appendChild(empty);
    _selectedItemIndex = null;
    _selectedItemId = null;
    return;
  }
  var selectedFound = false;
  items.forEach(function (item, idx) {
    var itemIndex = item && item.index != null ? Number(item.index) : idx;
    var isSelected =
      selectedIndex != null ? Number(selectedIndex) === itemIndex : idx === 0;
    var row = document.createElement("div");
    row.className = "item-row" + (isSelected ? " item-row--selected" : "");
    row.id = "item-row-" + (idx + 1);
    row.dataset.itemId =
      item && item.id ? String(item.id) : "item-" + itemIndex;
    row.dataset.itemIndex = String(itemIndex);
    row.dataset.action = "selectItem";
    row.setAttribute("role", "button");
    row.setAttribute("tabindex", "0");
    row.setAttribute("aria-selected", isSelected ? "true" : "false");
    var icon = document.createElement("div");
    icon.className = "item-icon";
    icon.setAttribute("aria-hidden", "true");
    icon.textContent = "IMG";
    var name = document.createElement("span");
    name.className = "item-name";
    name.textContent = item && item.name ? String(item.name) : "Objeto";
    var type = document.createElement("span");
    type.className = "item-type-badge";
    type.textContent = item && item.type ? String(item.type) : "Desconocido";
    row.appendChild(icon);
    row.appendChild(name);
    row.appendChild(type);
    rowsContainer.appendChild(row);
    if (isSelected) {
      selectedFound = true;
      _selectedItemIndex = Number(row.dataset.itemIndex);
      _selectedItemId = row.dataset.itemId;
    }
  });
  if (!selectedFound && rowsContainer.firstElementChild) {
    var firstRow = rowsContainer.querySelector(".item-row");
    if (firstRow) selectInventoryRow(firstRow);
  }
}

/* ── Render dinámico del loot de tesoro ── */
function renderLootList(loot) {
  var container = document.getElementById("treasure-loot-list");
  if (!container || !Array.isArray(loot)) return;
  container.innerHTML = "";
  var selectedFound = false;
  loot.forEach(function (item, idx) {
    var isSelected = !!item.selected;
    if (isSelected) {
      selectedFound = true;
      _selectedLootIndex = idx;
    }
    var div = document.createElement("div");
    div.className = "loot-item" + (isSelected ? " loot-item--selected" : "");
    div.dataset.lootIndex = String(idx);
    div.innerHTML =
      '<div class="loot-item__icon">' +
      (item.icon || "⭐") +
      "</div>" +
      '<div class="loot-item__info">' +
      '<div class="loot-item__name">' +
      (item.name || "") +
      "</div>" +
      '<div class="loot-item__desc">' +
      (item.desc || "") +
      "</div>" +
      "</div>" +
      '<span class="loot-rarity loot-rarity--' +
      (item.rarity || "comun") +
      '">' +
      (item.rarity || "comun") +
      "</span>";
    container.appendChild(div);
  });
  if (!selectedFound) {
    _selectedLootIndex = loot.length > 0 ? 0 : null;
  }
}

/* ── Render slots de guardado ── */
function renderSaveSlots(saveSlotsInfo) {
  if (!saveSlotsInfo || !Array.isArray(saveSlotsInfo.slots)) return;
  saveSlotsInfo.slots.forEach(function (slot) {
    var n = slot.slot;
    var row = document.getElementById("save-slot-row-" + n);
    if (!row) return;
    row.classList.toggle("save-slot-row--empty", !!slot.empty);
    row.classList.toggle(
      "save-slot-row--active",
      n === (saveSlotsInfo.selectedSlot || 1),
    );
    setNodeText("save-slot-icon-" + n, slot.heroIcon || "💭");
    setNodeText(
      "save-slot-label-" + n,
      slot.heroLabel || "heroe · " + formatHeroTypeLabel(slot.heroType),
    );
    if (slot.empty) {
      setNodeText("save-slot-details-" + n, "—");
      setNodeText("save-slot-date-" + n, "");
      setNodeText("save-slot-type-" + n, "vacío");
    } else {
      var det =
        "hp " +
        slot.hp +
        "/" +
        slot.hpMax +
        " · sala " +
        slot.roomNumber +
        " · " +
        (slot.dungeonTheme || "?");
      setNodeText("save-slot-details-" + n, det);
      setNodeText("save-slot-date-" + n, slot.savedAt || "");
      setNodeText(
        "save-slot-type-" + n,
        slot.saveType === "auto" ? "guardado automático" : "guardado manual",
      );
    }
  });
  if (saveSlotsInfo.selectedSlot) {
    selectSaveSlotRow(saveSlotsInfo.selectedSlot);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  renderHeroPortraitAssets();

  var loreOverlay = document.getElementById("lore-window-overlay");
  if (loreOverlay) {
    loreOverlay.addEventListener("click", function (e) {
      if (e.target === loreOverlay) {
        dismissLoreWindow();
      }
    });
  }

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      if (_loreVisible) {
        e.preventDefault();
        dismissLoreWindow();
        return;
      }

      closeCombatTacticsMenu();
    }
  });

  document.addEventListener("keydown", function (e) {
    var target = e.target;
    if (
      target &&
      (target.tagName === "INPUT" ||
        target.tagName === "TEXTAREA" ||
        target.isContentEditable)
    ) {
      return;
    }

    if (e.key === "F5") {
      e.preventDefault();
      sendCommand("quickSave", {});
      return;
    }

    if (e.key === "F9") {
      e.preventDefault();
      sendCommand("quickLoad", {});
      return;
    }

    if (e.key === "i" || e.key === "I") {
      e.preventDefault();
      sendCommand("toggleInventory", {});
      return;
    }

    if (_currentScreen === "inventory" && e.key === "ArrowUp") {
      e.preventDefault();
      sendCommand("inventoryPrevious", {});
      return;
    }

    if (_currentScreen === "inventory" && e.key === "ArrowDown") {
      e.preventDefault();
      sendCommand("inventoryNext", {});
      return;
    }

    if (_currentScreen === "combat" && (e.key === "r" || e.key === "R")) {
      e.preventDefault();
      sendCommand("retreatCombat", {});
    }
  });

  document.addEventListener("click", function (event) {
    var shell = document.getElementById("combat-tactics-shell");
    if (!shell) return;
    if (shell.contains(event.target)) return;
    closeCombatTacticsMenu();
  });

  /* ── Selección de héroe (local) ── */
  document
    .querySelectorAll('[data-action="selectHero"]')
    .forEach(function (card) {
      card.addEventListener("click", function () {
        if (card.dataset.state === "disabled") return;
        var heroType = card.dataset.hero || "guerrero";
        selectHeroCard(heroType);
        sendCommand("selectHero", { heroType: heroType });
      });
      card.addEventListener("keydown", function (e) {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          if (card.dataset.state === "disabled") return;
          var heroType = card.dataset.hero || "guerrero";
          selectHeroCard(heroType);
          sendCommand("selectHero", { heroType: heroType });
        }
      });
    });

  /* ── Selección de loot (local + backend) ── */
  var lootList = document.getElementById("treasure-loot-list");
  if (lootList) {
    lootList.addEventListener("click", function (e) {
      var row = e.target.closest(".loot-item");
      if (!row) return;
      lootList.querySelectorAll(".loot-item").forEach(function (item) {
        item.classList.remove("loot-item--selected");
      });
      row.classList.add("loot-item--selected");
      var lootIndex = Number(row.dataset.lootIndex || 0);
      _selectedLootIndex = Number.isFinite(lootIndex) ? lootIndex : 0;
      sendCommand("selectLoot", { lootIndex: _selectedLootIndex });
    });
  }

  /* ── Selección de slot (local) ── */
  var savesScreen = document.getElementById("screen-saves");
  if (savesScreen) {
    savesScreen.addEventListener("click", function (e) {
      var row = e.target.closest('[data-action="selectSaveSlot"]');
      if (!row) return;
      var slot = Number(row.dataset.slot);
      if (!Number.isFinite(slot)) return;
      sendCommand("selectSaveSlot", { slot: slot });
    });
  }

  /* ── Selección de item en inventario ── */
  var inventoryRows = document.getElementById("s3-item-rows");
  if (inventoryRows) {
    inventoryRows.addEventListener("click", function (event) {
      var row = event.target.closest('[data-action="selectItem"]');
      if (!row) return;
      selectInventoryRow(row);
      if (Number.isFinite(_selectedItemIndex)) {
        sendCommand("selectItem", { itemIndex: _selectedItemIndex });
      }
    });
    inventoryRows.addEventListener("keydown", function (event) {
      if (event.key !== "Enter" && event.key !== " ") return;
      var row = event.target.closest('[data-action="selectItem"]');
      if (!row) return;
      event.preventDefault();
      selectInventoryRow(row);
      if (Number.isFinite(_selectedItemIndex)) {
        sendCommand("selectItem", { itemIndex: _selectedItemIndex });
      }
    });
  }

  /* ── Filtro local de categorías ── */
  document
    .querySelectorAll('[data-action="filterCategory"]')
    .forEach(function (el) {
      el.addEventListener("click", function () {
        document.querySelectorAll(".cat-btn").forEach(function (b) {
          b.classList.remove("cat-btn--active");
          b.setAttribute("aria-pressed", "false");
        });
        el.classList.add("cat-btn--active");
        el.setAttribute("aria-pressed", "true");
      });
    });

  /* ── Botones de acción → Java via alert bridge ── */
  var LOCAL_ACTIONS = {
    selectItem: true,
    filterCategory: true,
    selectHero: true,
    selectSaveSlot: true,
  };
  document.querySelectorAll("[data-action]").forEach(function (el) {
    var action = el.dataset.action;
    if (LOCAL_ACTIONS[action]) return;
    el.addEventListener("click", function () {
      if (el.dataset.state === "disabled" || el.dataset.state === "cooldown")
        return;
      if (action === "openWorldLore") {
        openWorldLoreWindow();
        return;
      } else if (action === "dismissLoreWindow") {
        dismissLoreWindow();
        return;
      } else if (action === "goToHeroSelect") {
        startNewCampaignNarrativeFlow();
        sendCommand("goToHeroSelect", {});
        return;
      } else if (action === "newGame") {
        startNewCampaignNarrativeFlow();
        sendCommand("newGame", {});
        return;
      } else if (action === "toggleCombatTacticsMenu") {
        toggleCombatTacticsMenu();
        return;
      } else if (action === "heroNewGame") {
        var selectedTheme = el.dataset.theme || "poison";
        _pendingRunStartLore = {
          theme: selectedTheme,
          heroType: _selectedHeroType || "guerrero",
          includeHeroLore: !_heroSelectionLocked && !_loreState.heroStoryShown,
        };

        sendCommand("heroNewGame", {
          heroType: _selectedHeroType || "guerrero",
          theme: selectedTheme,
        });
        return;
      } else if (action === "startGame") {
        if (!_loreState.worldShown) {
          _loreState.worldShown = true;
          enqueueLoreWindow(buildWorldLoreEntry());
        }
        enqueueLoreWindow(buildDungeonLoreEntry(el.dataset.theme || "poison"));
        sendCommand("startGame", {
          theme: el.dataset.theme || "poison",
          heroType: _selectedHeroType || "guerrero",
        });
        return;
      }

      if (action === "useItem") {
        sendCommand("useItem", {
          itemIndex: Number.isFinite(_selectedItemIndex)
            ? _selectedItemIndex
            : null,
          itemId: _selectedItemId,
        });
      } else if (action === "loadGame") {
        var loadSlot = Number(el.dataset.slot || 1);
        sendCommand("loadGame", { slot: loadSlot });
      } else if (action === "saveGame") {
        var picker = document.getElementById("save-slot-picker");
        var saveSlot = picker ? Number(picker.value || 1) : 1;
        if (!Number.isFinite(saveSlot)) saveSlot = 1;
        sendCommand("saveGame", { slot: saveSlot });
      } else if (action === "attack") {
        sendCommand("attack", { targetId: "current" });
      } else if (action === "useSkill") {
        sendCommand("useSkill", {});
      } else if (action === "setCombatStyle") {
        sendCommand("setCombatStyle", {
          style: el.dataset.style || "balanced",
        });
        closeCombatTacticsMenu();
      } else if (action === "applyBuff") {
        sendCommand("applyBuff", { type: el.dataset.buff || "power" });
        closeCombatTacticsMenu();
      } else if (action === "saveCombatCheckpoint") {
        sendCommand("saveCombatCheckpoint", {});
      } else if (action === "rollbackCombatCheckpoint") {
        sendCommand("rollbackCombatCheckpoint", {});
      } else if (action === "showStats") {
        sendCommand("showStats", {});
      } else if (action === "closeStats") {
        sendCommand("closeStats", {});
      } else if (action === "openSaves") {
        sendCommand("openSaves", {});
      } else if (action === "saveToSlot") {
        sendCommand("saveToSlot", {});
      } else if (action === "loadFromSlot") {
        sendCommand("loadFromSlot", {});
      } else if (action === "takeLoot") {
        sendCommand("takeLoot", {});
      } else if (action === "selectLoot") {
        sendCommand("selectLoot", {
          lootIndex: Number.isFinite(_selectedLootIndex)
            ? _selectedLootIndex
            : 0,
        });
      } else if (action === "skipLoot") {
        sendCommand("skipLoot", {});
      } else if (action === "restoreGame") {
        sendCommand("restoreGame", {});
      } else if (action === "exitGame") {
        sendCommand("exitGame", {});
      } else if (action === "backToMenu") {
        sendCommand("openMainMenu", {});
      } else {
        sendCommand(action, {});
      }
    });
  });
});

/* ── updateGameState: Java llama engine.executeScript("window.updateGameState(..)") ── */
window.updateGameState = function (state) {
  if (!state) return;
  var previousScreen = _currentScreen;

  function setText(id, val) {
    var el = document.getElementById(id);
    if (el)
      el.textContent = val !== null && val !== undefined ? String(val) : "";
  }
  function setWidth(id, val) {
    var el = document.getElementById(id);
    if (el) el.style.width = val;
  }
  function renderMinimap(symbols, totalRooms, currentRoomOneBased) {
    var row = document.getElementById("s1-minimap-row");
    if (!row) return;
    var items = Array.isArray(symbols) ? symbols.slice() : [];
    if (items.length === 0 && totalRooms > 0) {
      for (var i = 0; i < totalRooms; i++) {
        if (i === Math.max(0, currentRoomOneBased - 1)) items.push("current");
        else if (i === totalRooms - 1) items.push("boss");
        else items.push("unknown");
      }
    }
    row.innerHTML = "";
    items.forEach(function (symbol, idx) {
      var node = document.createElement("div");
      node.classList.add("minimap-node");
      node.dataset.roomIndex = String(idx);
      switch (symbol) {
        case "cleared":
          node.classList.add("minimap-node--cleared");
          node.title = "Sala despejada";
          node.textContent = "\u00b7";
          break;
        case "current":
          node.classList.add("minimap-node--active");
          node.title = "Sala actual";
          node.textContent = "\u2694";
          break;
        case "boss":
          node.classList.add("minimap-node--boss");
          node.title = "Jefe final";
          node.textContent = "\ud83d\udc80";
          break;
        default:
          node.classList.add("minimap-node--unknown");
          node.title = "Sala desconocida";
          node.textContent = "?";
          break;
      }
      row.appendChild(node);
      if (idx < items.length - 1) {
        var connector = document.createElement("span");
        connector.className = "minimap-connector";
        connector.setAttribute("aria-hidden", "true");
        connector.textContent = "\u2014";
        row.appendChild(connector);
      }
    });
  }

  function renderCombatStatusEffects(combatTactics) {
    var slots = document.querySelectorAll("#s2-status-effects .status-slot");
    if (!slots || slots.length === 0) {
      return;
    }

    var effects = [];
    if (combatTactics) {
      var poisonTurns = Number(combatTactics.poisonTurns || 0);
      var poisonDamage = Number(combatTactics.poisonDamage || 0);
      if (poisonTurns > 0) {
        effects.push({
          key: "veneno",
          label: "VEN",
          title:
            poisonDamage > 0
              ? "Veneno activo: " +
                poisonDamage +
                " daño por turno (" +
                poisonTurns +
                " turnos)."
              : "Veneno activo (" + poisonTurns + " turnos).",
        });
      }

      var offensiveStacks = Number(combatTactics.offensiveBuffStacks || 0);
      if (offensiveStacks > 0) {
        effects.push({
          key: "poder",
          label: "ATK+" + offensiveStacks,
          title:
            "Buff ofensivo activo (" + offensiveStacks + " acumulaciones).",
        });
      }

      var guardStacks = Number(combatTactics.guardBuffStacks || 0);
      if (guardStacks > 0) {
        effects.push({
          key: "guardia",
          label: "DEF+" + guardStacks,
          title: "Buff de guardia activo (" + guardStacks + " acumulaciones).",
        });
      }

      if (combatTactics.defenseActive) {
        effects.push({
          key: "defensa",
          label: "DEF",
          title: "Defensa activa durante este turno.",
        });
      }
    }

    for (var i = 0; i < slots.length; i++) {
      var slot = slots[i];
      var effect = effects[i];
      if (effect) {
        slot.textContent = effect.label;
        slot.dataset.effect = effect.key;
        slot.title = effect.title;
        slot.setAttribute(
          "aria-label",
          "Efecto " + (i + 1) + ": " + effect.title,
        );
      } else {
        slot.textContent = "\u2014";
        slot.dataset.effect = "";
        slot.title = "";
        slot.setAttribute("aria-label", "Efecto " + (i + 1) + ": vacio");
      }
    }
  }

  /* Header compartido */
  var prog = "Sala " + (state.room || 0) + " / " + (state.totalRooms || 0);
  var gold = "\u29a1 " + (state.gold || 0) + " Monedas";
  var hpPct = (state.playerHpPct || 0) + "%";
  var hpText = (state.playerHp || 0) + " / " + (state.playerHpMax || 0);
  ["", "-combat", "-inv"].forEach(function (sfx) {
    setText("hdr-dungeon-name" + sfx, state.dungeonName || "");
    setText(
      "hdr-hero-type" + sfx,
      "Clase · " + formatHeroTypeLabel(state.heroType),
    );
    setText("hdr-dungeon-theme" + sfx, state.dungeonTheme || "");
    setText("hdr-room-progress" + sfx, prog);
    setText("hdr-gold" + sfx, gold);
    setWidth("hdr-player-hp-fill" + sfx, hpPct);
    setText("hdr-player-hp-text" + sfx, hpText);
  });

  /* Tema visual */
  if (state.theme) {
    document.body.classList.remove(
      "theme-fire",
      "theme-ice",
      "theme-poison",
      "theme-dark",
    );
    document.body.classList.add("theme-" + state.theme);
  }

  renderHeroPortraitAssets();
  renderSceneAssets(state);

  /* Pantalla activa */
  if (state.screen) {
    _currentScreen = String(state.screen);
    var screenMap = {
      menu: "screen-menu",
      hero: "screen-hero",
      stats: "screen-stats",
      exploration: "screen-exploration",
      combat: "screen-combat",
      inventory: "screen-inventory",
      treasure: "screen-treasure",
      saves: "screen-saves",
      gameover: "screen-gameover",
    };
    document.querySelectorAll(".screen").forEach(function (s) {
      s.classList.remove("active");
    });
    var target = screenMap[state.screen];
    if (target) document.getElementById(target).classList.add("active");
  }

  /* Pantalla Héroe: sincronizar selección si Java la envía */
  if (state.heroType) {
    selectHeroCard(state.heroType);
  }
  _heroSelectionLocked = !!state.heroSelectionLocked;
  _completedThemes = Array.isArray(state.completedThemes)
    ? state.completedThemes.map(function (theme) {
        return String(theme).toLowerCase();
      })
    : [];
  _nextCampaignTheme = state.nextCampaignTheme
    ? String(state.nextCampaignTheme).toLowerCase()
    : "";
  _campaignThemeOrder = Array.isArray(state.campaignThemeOrder)
    ? state.campaignThemeOrder.map(function (theme) {
        return String(theme).toLowerCase();
      })
    : _campaignThemeOrder;
  applyHeroScreenRestrictions();

  if (
    _pendingRunStartLore &&
    previousScreen === "hero" &&
    _currentScreen === "exploration"
  ) {
    if (_pendingRunStartLore.includeHeroLore && !_loreState.heroStoryShown) {
      _loreState.heroStoryShown = true;
      enqueueLoreWindow(buildHeroLoreEntry(_pendingRunStartLore.heroType));
    }

    enqueueLoreWindow(buildDungeonLoreEntry(_pendingRunStartLore.theme));
    _pendingRunStartLore = null;
  }

  /* Exploración */
  setText("s1-room-name", state.roomName || "");
  setText("s1-room-desc", state.roomDesc || "");
  setText("s1-difficulty", state.roomDifficulty || "");
  setText("s1-has-treasure", state.roomHasTreasure ? "S\u00ed" : "No");
  setText("s1-has-enemy", state.roomHasEnemy ? "S\u00ed" : "No");
  renderMinimap(
    state.minimapSymbols,
    Number(state.totalRooms || 0),
    Number(state.room || 1),
  );

  if (Array.isArray(state.eventLog)) {
    var elog = document.getElementById("s1-event-log");
    if (elog) {
      elog.innerHTML = "";
      if (state.eventLog.length === 0) {
        var ph = document.createElement("p");
        ph.className = "log-entry log-entry--placeholder";
        ph.textContent = "Sin eventos a\u00fan";
        elog.appendChild(ph);
      } else {
        state.eventLog.forEach(function (line) {
          var p = document.createElement("p");
          p.className = "log-entry";
          p.textContent = line;
          elog.appendChild(p);
        });
      }
    }
  }

  /* Combate */
  if (state.enemy) {
    var en = state.enemy;
    var normalizedTier = normalizeEnemyTier(en.tier);
    setText("s2-enemy-name-duel", en.name || "");
    setText("s2-enemy-name", en.name || "");
    setText("s2-enemy-hp-text", (en.hp || 0) + " / " + (en.hpMax || 0));
    setWidth("s2-enemy-hp-fill", (en.hpPct || 0) + "%");
    var tierEl = document.getElementById("s2-enemy-tier");
    if (tierEl) {
      tierEl.textContent = formatEnemyTierLabel(en.tier);
      tierEl.dataset.tier = normalizedTier;
    }
  }

  if (state.resource) {
    var rs = state.resource;
    var label = formatResourceLabel(rs.type);
    var cur = Number(rs.current || 0);
    var max = Number(rs.max || 0);
    var pct = Number(rs.pct || 0);
    setText("s2-resource-type", label);
    setText("s2-resource-value", cur + " / " + max);
    setText("s2-resource-pct", pct + "%");
  }

  if (state.combatTactics) {
    var ct = state.combatTactics;
    setText("s2-style-current", ct.style || "Balanceado");
    setText(
      "s2-buff-power",
      ct.offensiveBuffStacks != null ? ct.offensiveBuffStacks : 0,
    );
    setText(
      "s2-buff-guard",
      ct.guardBuffStacks != null ? ct.guardBuffStacks : 0,
    );
  }
  renderCombatStatusEffects(state.combatTactics || null);

  if (Array.isArray(state.combatLog)) {
    var clog = document.getElementById("s2-combat-log");
    if (clog) {
      var zl = clog.querySelector(".zone-label");
      clog.innerHTML = "";
      if (zl) clog.appendChild(zl);
      state.combatLog.forEach(function (line) {
        var p = document.createElement("p");
        p.className = "log-entry";
        p.textContent = line;
        clog.appendChild(p);
      });
    }
  }

  /* Inventario */
  if (state.inventory) {
    setText("s3-capacity-current", state.inventory.itemCount || 0);
    setText("s3-capacity-max", state.inventory.maxCapacity || 0);
  }
  if (Array.isArray(state.inventoryItems)) {
    renderInventoryItems(state.inventoryItems, state.selectedItemIndex);
  }
  if (
    state.selectedItemIndex !== undefined &&
    state.selectedItemIndex !== null
  ) {
    _selectedItemIndex = Number(state.selectedItemIndex);
    if (Array.isArray(state.inventoryItems)) {
      var selected = state.inventoryItems.find(function (item) {
        return Number(item.index) === Number(state.selectedItemIndex);
      });
      if (selected && selected.id) _selectedItemId = String(selected.id);
    }
  }
  if (state.selectedItem) {
    var si = state.selectedItem;
    setText("s3-item-name", si.name || "");
    setText("s3-item-desc", si.desc || "");
    setText("s3-item-effect", "Efecto: " + (si.effect || ""));
    setText("s3-item-valor", si.valor != null ? String(si.valor) : "");
    setText("s3-item-peso", si.peso != null ? si.peso + " kg" : "");
    var typeEl = document.getElementById("s3-item-type");
    if (typeEl) {
      typeEl.textContent = si.type || "";
      if (si.type) typeEl.dataset.type = si.type.toLowerCase();
    }
  }

  /* Estadísticas */
  if (state.stats) {
    var st = state.stats;
    var iconMap = {
      guerrero: "\ud83d\udde1\ufe0f",
      mago: "\ud83d\udd2e",
      arquero: "\ud83c\udff9",
    };
    var isEmptyStats = st.heroType === "sin_partida";
    setText(
      "stats-hero-icon",
      isEmptyStats
        ? "\ud83d\udcad"
        : iconMap[st.heroType] || "\ud83d\udde1\ufe0f",
    );
    setText(
      "stats-hero-class",
      isEmptyStats ? "Sin partida activa" : formatHeroTypeLabel(st.heroType),
    );
    setText(
      "stats-hero-type",
      isEmptyStats
        ? "inicia o carga una partida"
        : "clase \u00b7 " + (st.heroType || ""),
    );
    setText("stats-hp", st.heroHp || 0);
    setText("stats-atk", st.heroAtk || 0);
    setText("stats-def", st.heroDef || 0);
    setText("stats-spd", st.heroSpeed || 0);
    setText("stats-rooms", st.roomsExplored || 0);
    setText("stats-enemies", st.enemiesDefeated || 0);
    setText("stats-gold", st.goldTotal || 0);
    setText("stats-items", st.itemsCollected || 0);
    setWidth("stats-hp-fill", (st.heroHpPct || 0) + "%");
    setText("stats-hp-text", (st.heroHp || 0) + " / " + (st.heroHpMax || 0));
    setText("stats-gold-display", "\u29a1 " + (st.goldTotal || 0) + " Monedas");
    setText("stats-dungeon", st.dungeonName || "\u2014");
  }

  /* Sala de Tesoro */
  if (state.treasure) {
    var tr = state.treasure;
    setText("treasure-enemy-name", tr.enemyDefeated || "");
    setText("treasure-exp", "+" + (tr.expGained || 0));
    setText("treasure-gold", "+" + (tr.goldGained || 0));
    setText("treasure-rooms", tr.roomsExplored || 0);
    setText("treasure-enemies", tr.enemiesDefeated || 0);
    setText("treasure-total-gold", tr.goldTotal || 0);
    setText("treasure-items", tr.itemsCollected || 0);
    setText("treasure-hp", (tr.hpCurrent || 0) + "/" + (tr.hpMax || 0));
    setText("treasure-checkpoint-room", tr.checkpointRoom || 1);
    if (Array.isArray(tr.loot)) renderLootList(tr.loot);
  }

  /* Slots de guardado */
  if (state.saveSlotsInfo) renderSaveSlots(state.saveSlotsInfo);

  /* Game Over */
  if (state.gameOver) {
    var go = state.gameOver;
    setText(
      "gameover-hero-type",
      "el " + formatHeroTypeLabel(go.heroType).toLowerCase(),
    );
    setText(
      "gameover-context",
      "derrotado por " + (go.defeatedBy || "un enemigo"),
    );
    setText("gameover-rooms", go.roomsExplored || 0);
    setText("gameover-enemies", go.enemiesDefeated || 0);
    setText("gameover-gold", go.goldGained || 0);
    setText("gameover-turns", go.turnsPlayed || 0);
    var restoreBtn = document.getElementById("btn-restore-game");
    if (restoreBtn)
      restoreBtn.dataset.state = go.hasSaveToRestore ? "default" : "disabled";
  }

  /* Estados de botones */
  if (state.buttons && typeof state.buttons === "object") {
    Object.keys(state.buttons).forEach(function (id) {
      var el = document.getElementById(id);
      if (el) el.dataset.state = state.buttons[id];
    });
  }

  var tacticsBtn = document.getElementById("btn-combat-tactics");
  if (tacticsBtn && tacticsBtn.dataset.state === "disabled") {
    closeCombatTacticsMenu();
  }

  evaluateLoreTriggers(state);
};
