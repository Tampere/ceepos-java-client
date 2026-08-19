# CPU Payment Client

Itsenäinen Java-asiakasohjelma Ceepos-verkkokauppamaksurajapinnalle (CPU) ("Compact Payment Unit" / Paytrail-tuoteperhe).

## Tuetut toiminnallisuudet

Kirjasto tukee tällä hetkellä ainoastaan CPU-järjestelmän "Web Shop" -rajapintaan lähetettäviä maksupyyntöjä sekä käsittelee rajapinnan palauttamia vastauksia. Maksupyyntöjen perumiseen liittyvää logiikkaa ei ole vielä toteutettu.

## CPU maksuprosessi

1. Käyttäjä → Verkkokauppa: pyytää maksun aloitusta
2. Verkkokauppa -> CPU: CpuPaymentClient lähettää maksupyynnön CPU:lle
3. CPU → Verkkokauppa: vastaa maksuosoitteella, johon käyttäjä tullaan ohjaamaan
4. Verkkokauppa → käyttäjä: ohjaa maksusivulle
5. Käyttäjä ↔ CPU: maksaminen ja ohjaaminen maksupalveluntarjoajan palveluun
6. CPU → Verkkokauppa: ilmoittaa maksun tuloksen asynkronisesti taustalla
7. Maksun jälkeen CPU ohjaa käyttäjän takaisin verkkokauppaan
8. Verkkokauppa → käyttäjä: näyttää tilauksen tilan

## Toimintaperiaate

Kirjasto tarjoaa käyttöön `CpuPaymentClient`-luokan, jolla kolme julkista metodia:

- `createPayment` maksupyyntöjen lähettämiseen CPU-rajapinnalle
- `parseCallback` CPU:lta verkkokauppaan saapuvien asynkronisien tilaviestien käsittelyyn
- `parseReturn` maksupalvelusta verkkokauppaan palattaessa kutsuttavaan GET-pyyntöön liitettävien kyselyparametrien käsittelyyn

### Tiivistekentän tarkistus

Jokaiseen CpuPaymentClientin ja CPU-palveluntarjoajan väliseen HTTP(s) pyyntöön lisätään tiiviste-kenttä, joka muodostetaan yhteisestä jaetusta salaisuudesta `CPU_SECRET` ja pyynnön sisällöstä. Tiivisteen avulla osapuolet voivat varmistua, että kolmas osapuoli ei ole puuttunut viestiliikenteeseen. CpuPaymentClientin julkisten metodien palauttama olio sisältää `checksumValid` kentän, joka kertoo vastaako pyynnössä olevan tiivisteen arvo oletettua.

**HUOM! CpuPaymentClient luokan käyttäjän tulee itse käsitellä tilanne, jossa `checksumValid=false`**

## Vaadittavat ympäristömuuttujat

| Muuttuja              | Esimerkkiarvo                                | Kuvaus                                                                                                         |
| --------------------- | -------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| `CPU_URL`             | `https://verkkomaksutesti.cpu.fi/maksu.html` | CPU-rajapinnan osoite, johon maksupyynnöt lähetetään                                                           |
| `CPU_SOURCE`          | `cpu_user_sourc`                             | Kauppiaan Source-tunnus                                                                                        |
| `CPU_SECRET`          | `top_secret`                                 | Kauppiaskohtainen salaisuus tarkistussumman laskentaan ja tarkistukseen                                        |
| `CPU_PRODUCTCODE`     | `demo_004`                                   | Tuoterivien tuotekoodi                                                                                         |
| `CPU_VATCLASS`        | `0`                                          | Tuoterivien ALV-luokka                                                                                         |
| `CPU_DEVELOPERPREFIX` | (tyhjä)                                      | Tilausviitteiden etuliite, jolla erotetaan eri ympäristöjen (esim. kehittäjien) tilaus-id:t toisistaan CPU:ssa |

## Käyttöesimerkki

```java
CpuPaymentClient client = new CpuPaymentClient(
    cpuUrl, cpuSource, cpuSecret, cpuProductCode, cpuVatClass, developerPrefix);

// Maksun luonti
CpuPaymentOrderDetails details = new CpuPaymentOrderDetails(
    order.getId().toString(),
    "Tilaus " + order.getId(),
    order.getEmail(),
    user.getFirstname(),
    user.getLastname(),
    returnAddress,
    notificationAddress,
    List.of(new CpuPaymentProductLine(product.getPrice(), product.getDescription())));

Optional<CpuPaymentClient.PaymentResult> result = client.createPayment(details);
result.ifPresent(r -> {
    // Myös CPU:n maksupyyntöön antamassa vastauksessa on oma tiivisteensä,
    // joka kannattaa tarkistaa ennen käyttäjän ohjaamista maksuosoitteeseen.
    if (!r.checksumValid()) {
        throw new CpuPaymentException("Virheellinen tarkistussumma CPU:n maksuvastauksessa");
    }
    redirectUserTo(r.response().paymentAddress());
});

// Asynkroninen callback (POST-body CPU:lta)
CpuPaymentClient.NotificationResult callback = client.parseCallback(rawRequestBody);

// Selaimen paluu (GET-kyselyparametrit)
CpuPaymentClient.NotificationResult returned = client.parseReturn(queryParams);

// Tiivisteen tarkistus: callback.checksumValid() (ja returned.checksumValid())
// kertoo, vastaako viestin mukana tullut Hash-kenttä CPU_SECRET-salaisuudesta
// ja viestin sisällöstä lasketusta tiivisteestä. Jos tiiviste ei täsmää,
// viestiä ei tule käsitellä maksun tilan päivityksenä.
if (!callback.checksumValid()) {
    throw new CpuPaymentException("Virheellinen tarkistussumma CPU:n callback-viestissä");
}
```
## Asentaminen 

CPU Payment Client on julkaistu Maven-pakettina GitHub Packages -rekisteriin. Alla ohjeet paketin asentamiseen paikallisesti.

### 1. Luo GitHubin Personal Access Token

- Mene GitHubissa reittiä: **Settings** → **Developer settings** → **Personal access tokens (classic)**.
- Tarvittava scope (laajuus): ainoastaan `read:packages`.

### 2. Lisää tunnukset tiedostoon ~/.m2/settings.xml

Tiedoston `<id>`-elementin on vastattava `server/pom.xml`-tiedostossa määriteltyä repositoryn id:tä (`github`):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>GITHUB_KÄYTTÄJÄTUNNUS</username>
      <password>GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

Jos `~/.m2/settings.xml` on jo olemassa (esim. muiden projektien jäljiltä), älä ylikirjoita tiedostoa. Lisää ainoastaan yllä oleva `<server>`-lohko olemassa olevan `<servers>`-elementin sisään.

### 3. Lisää riippuvuus projektisi pom.xml-tiedostoon

Lisää seuraava riippuvuus (dependency) `server/pom.xml`-tiedoston `<dependencies>`-osion sisään:

```xml
<dependency>
  <groupId>fi.tampere</groupId>
  <artifactId>cpupayment-client</artifactId>
  <version>0.1</version>
</dependency>
```

### 4. Varmista toimivuus

Suorita seuraavat komennot:

```bash
cd server
mvn clean install
```

Mavenin pitäisi nyt ladata riippuvuus `fi.tampere:cpupayment-client:0.1` osoitteesta `https://maven.pkg.github.com/Tampere/cpupayment-client`.
