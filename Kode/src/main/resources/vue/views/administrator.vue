<template id="administrator">
  <div id="administrator-app">
    <header id="header">
      <h1>Parkeringstjenesten - Administrator</h1>
      <div class="tilbakeDiv">
        <a class="tilbake tilbakeIndex" href="/index">Tilbake Index</a>
        <a class="tilbake tilbakeMinSide" href="/bruker">Tilbake Min Side</a>
      </div>
      <div>
        <p>Logget inn som {{bruker.navn}}!</p>
      </div>
    </header>
    <div class="mineParkeringsplasser">
      <h2>Alle parkeringsplasser:</h2>
      <article v-bind:key="plass.id" v-for="plass in parkering" >
        <h5>Beskrivelse</h5>
        <p>{{plass.beskrivelse}}</p>
        <h5>Koordinater</h5>
        <p>{{plass.breddegrad}} bredde, {{plass.lengdegrad}} lengde</p>
        <p>Pris pr time: {{plass.pris}}</p>
        <p>Fra: {{dateToString(plass.fra)}}</p>
        <p>Til: {{dateToString(plass.til)}}</p>

        <p>Antall: {{plass.antallPlasser}}</p>
        <p>Antall handicap: {{plass.antallHandicap}}</p>
        <form action="/api/parkeringsplass/fjernAdmin" method="post">
          <input type="hidden" name="parkeringsplassId" id="parkeringsplassId" v-model="plass.id">
          <input type="submit" value="Fjern">
        </form>
      </article>
    </div>
  </div>
</template>

<script>
Vue.component("administrator", {
  template: "#administrator",
  data: () => ({
    BP: [],
    parkering: [],
    brukere: [],
    bruker: null,
  }),
  created() {
    fetch(`/api/administrator`)
        .then(res => res.json())
        .then(res => this.BP = res)
        .then(res => this.bruker = this.BP[0][0])
        .then(res => this.parkering = this.BP[1])
        .then(res => this.brukere = this.BP[2])
        .catch(() => alert("Error kunne ikke skaffe lista"));
  },
  methods:{
    dateToString: function(x) {
      var string = x.dayOfMonth + "/" + x.monthValue + "/" + x.year + " Kl: ";
      if(x.hour.toString().length === 1)
        string += "0"
      string += x.hour + ":"

      if(x.minute.toString().length === 1)
        string += "0"
      string += x.minute

      return string;
    }
  }
});
</script>

<style>
  #administrator-app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
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

  .tilbakeDiv {
    grid-area: knapp;
  }

  .tilbake {
    padding: 5%;
    height: fit-content;
    justify-self: center;
    margin: 1%;
    border-radius: 6%;
    background-color: whitesmoke;
    color: black;
    text-decoration: none;
  }
</style>