<template id="bruker">
  <div id="bruker-app">
    <header id="header">
      <h1>Parkeringstjenesten - Min side</h1>
      <a id="tilbake" href="/index">Tilbake</a>
      <div>
        <p>Logget inn som {{bruker.navn}}!</p>
      </div>
    </header>
    <div class="mineParkeringsplasser">
      <h2>Mine parkeringsplasser:</h2>
      <article v-bind:key="plass.id" v-for="plass in bruker.leierUt" >
        <h5>Beskrivelse</h5>
        <p>{{plass.beskrivelse}}</p>
        <h5>Koordinater</h5>
        <p>{{plass.breddegrad}} bredde, {{plass.lengdegrad}} lengde</p>
        <p>Pris pr time: {{plass.pris}}</p>
        <p>Fra: {{dateToString(plass.fra)}}</p>
        <p>Til: {{dateToString(plass.til)}}</p>

        <p>Antall: {{plass.antallPlasser}}</p>
        <p>Antall handicap: {{plass.antallHandicap}}</p>
        <form action="/api/parkeringsplass/fjern" method="post">
          <input type="hidden" name="parkeringsplassId" id="parkeringsplassId" v-model="plass.id">
          <input type="submit" value="Fjern">
        </form>
      </article>
    </div>
    <div class="mineOrdre">
      <h2>Mine ordre:</h2>
      <article v-bind:key="o.id" v-for="o in bruker.leierInn">
        <h5>Utleier: {{o.eiernavn}}</h5>
        <p>Fra: {{dateToString(o.fra)}}</p>
        <p>Til: {{dateToString(o.til)}}</p>
        <p>Handicap: {{handicapTrueFalse(o.handicap)}}</p>
        <form action="/api/ordre/fjern" method="post">
          <input type="hidden" name="ordreId" id="ordreId" v-model="o.id">
          <input type="submit" value="Fjern">
        </form>
      </article>
    </div>
    <div class="brukerInfo">
      <form action="/api/kortnummer/rediger" method="post">
        <label>Rediger kortnummer:</label>
        <input type="text" name="bruker.kortnummer" id="bruker.kortnummer"  v-model="bruker.kortnummer" pattern="[0-9]{16}" title="Må ha 16 nummer" required>
        <input type="submit" value="Rediger">
      </form>
      <form action="/api/regnr/leggTil" method="post">
        <label>Legg til regnr:</label>
        <input type="text" name="leggTilRegnr" id="leggTilRegnr" v-model="leggTilRegnr" pattern=".{1,12}" title="Må ha 1 til 12 tegn" required>
        <input type="submit" value="Legg til">
      </form>
      <div v-for="regnr in bruker.registreringsnummer">
        <form action="/api/regnr/slett" method="post">
          <label>{{regnr}}</label>
          <input type="hidden" name="slett" id="slett" v-model="regnr">
          <input type="submit" value="Slett">
        </form>
      </div>
    </div>
  </div>
</template>

<script>
Vue.component("bruker", {
  template: "#bruker",
  data: () => ({
    bruker: {
      navn: null,
      id: null,
      registreringsnummer: [],
      leierInn: [],
      leierUt: [],
      kortnummer: null
    },
    leggTilRegnr: null,
    endreRegnr: null
  }),
  created() {
    fetch(`/api/bruker`)
        .then(res => res.json())
        .then(res => this.bruker = res)
        .catch(() => alert("Error kunne ikke skaffe lista"));
  },
  methods: {
    dateToString: function(x) {
      var string = x.dayOfMonth + "/" + x.monthValue + "/" + x.year + " Kl: ";
      if(x.hour.toString().length === 1)
        string += "0"
        string += x.hour + ":"

      if(x.minute.toString().length === 1)
        string += "0"
        string += x.minute

      return string;
    },
    handicapTrueFalse: function(x) {
      if(x == true) {
        return "Ja";
      }
      return "Nei";
    }
  }
});
</script>

<style scoped>
body{
  box-sizing: border-box;
  background-color: whitesmoke;
}
#bruker-app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-areas:
    "header header"
    "parkeringsplasser ordre"
    "leggTilInfo leggTilInfo";
  margin: 5vw;
}

#header {
  grid-area: header;
  background-color: yellowgreen;
  margin: 2vw;
  margin-bottom: 0;
  border-radius: 1rem;
  display: grid;
  grid-auto-columns: 1fr 1fr 1fr;
  grid-template-areas:"overskrift knapp innlogget";
  justify-content: space-evenly;

}
header > h1 {
  margin: 2vw;
  grid-area: overskrift;
}
header > div{
  margin: 2vw;
  display: inline-flex;
  gap: 1rem;
  grid-area: innlogget;
}

#tilbake {
  grid-area: knapp;
  padding: 5%;
  height: fit-content;
  justify-self: center;
  margin: 1%;
  border-radius: 6%;
  background-color: whitesmoke;
  color: black;
  text-decoration: none;
}


.mineParkeringsplasser {
  grid-area: parkeringsplasser;
}

.mineParkeringsplasser > h2, .mineParkeringsplasser > article, .mineOrdre > h2, .mineOrdre > article {
  padding-top: 1vh;
  padding-bottom: 1vh;
}

.mineOrdre {
  grid-area: ordre;
}

.brukerInfo {
  grid-area: leggTilInfo;
}

.mineParkeringsplasser > h2, .mineParkeringsplasser > article, .mineOrdre > h2, .mineOrdre > article, .brukerInfo {
  background-color: grey;
  margin: 2vw;
  border-radius: 1rem;
}

</style>
