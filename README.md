# NewsFeedApp - RMA Projekat

Kratki opis
-----------
NewsFeedApp je Android mobilna aplikacija izrađena u sklopu projekta iz predmeta "Razvoj mobilnih aplikacija". Aplikacija prikazuje vijesti iz izvora (online API) i omogućava filtriranje, pregled detalja i spremanje vijesti lokalno.

Glavne funkcionalnosti
----------------------
- Prikaz vijesti u formatu feeda sa istaknutim (featured) i standardnim karticama
- Kategorije vijesti i brzo filtriranje (npr. Sve, Politika, Sport, Nauka, Tehnologija, Ljepota i zdravlje)
- Napredni filteri: vremenski opseg i lista neželjenih riječi
- Pregled detalja vijesti (slika, naslov, sažetak, izvor, datum)
- Spremanje vijesti lokalno u bazu podataka i rad u offline režimu (fallback na spremljene vijesti)
- Dohvat sličnih vijesti i upravljanje tagovima (u repozitoriju)
- Jedinični i instrumentacijski testovi (u projektu su primjeri testova)

Korištene tehnologije
---------------------
- Jezik: Kotlin
- UI: Jetpack Compose (Material3)
- Navigacija: Jetpack Navigation Compose
- Mreža: Retrofit (API servis: NewsApiService)
- Lokalna pohrana: Room (NewsDatabase, SavedNewsDAO)
- Asinhrono: Kotlin Coroutines
- Gradle (Kotlin DSL) i Gradle Wrapper

