<template id="index">
  <div id="index-app">
    <header id="header">
      <h1>Parkeringstjenesten</h1>
      <div class="tilDiv">
        <div v-if="bruker != null">
          <form action="api/administrator" method="post" v-if="bruker.admin" >
            <input type="submit" value="Til Adminside" >
          </form>
        </div>
        <form action="api/redirectBruker" method="post" v-if="bruker != null">
          <input type="hidden" name="redirectBrukerId" :value="bruker.id">
          <input type="submit" name="redirectBruker" value="Til brukersiden">
        </form>
      </div>
      <div v-if="bruker == null">
        <form v-for="b in brukere" action="api/loggInn" method="post">
          <input type="hidden" name="b.id" id="b.id" v-model="b.id">
          <input type="submit" id="b.navn" :value="b.navn">
        </form>
      </div>
      <div v-else>
        <p>Logget inn som {{bruker.navn}}!</p>
        <button v-on:click="bruker = null">Logg ut</button>

      </div>

    </header>
    <div class="leiUt" v-if="bruker != null">
      <h2>Lei ut parkeringsplass</h2>
      <form :action=`/api/lag` method="post">
        <div>
          <label>Fra: </label>
          <input type="datetime-local" name="leiUt.fra" id="leiUt.fra" v-model="leiUt.fra" required>
        </div>
        <div>
          <label>Til: </label>
          <input type="datetime-local" name="leiUt.til"  id="leiUt.til" v-model="leiUt.til" required>
        </div>
        <div>
          <label>Pris pr time: </label>
          <input type="number" step="any" name="leiUt.pris"  id="leiUt.pris" v-model="leiUt.pris" required>
        </div>
        <div>
          <label>Antall: </label>
          <input type="number" name="leiUt.antall"  id="leiUt.antall" v-model="leiUt.antall" required>
        </div>
        <div>
          <label>Antall handicap: </label>
          <input type="number" name="leiUt.antallHandicap"  id="leiUt.antallHandicap" v-model="leiUt.antallHandicap" required>
        </div>
        <div>
          <label>Lengdegrad: </label>
          <input type="number" name="leiUt.lengdegrad"  step="any" id="leiUt.lengdegrad" v-model="leiUt.lengdegrad" required>
        </div>
        <div>
          <label>Breddegrad: </label>
          <input type="number" name="leiUt.breddegrad"  step="any" id="leiUt.breddegrad" v-model="leiUt.breddegrad" required>
        </div>
        <div>
          <label>Beskrivelse: </label>
          <input type="text" name="leiUt.beskrivelse"  id="leiUt.beskrivelse" v-model="leiUt.beskrivelse" required>
        </div>
        <div>
          <input type="hidden" name="leiUt.brukerId"  id="leiUt.brukerId" :value="brukerId = this.bruker.id" required>
        </div>
        <input type="submit" value="Lei ut">
      </form>
    </div>
    <div class="leiUt" v-else>
      <h2>Logg inn for å leie ut</h2>
    </div>
    <div id="ledige">
      <article class="articleLedige" v-bind:key="plass.id" v-for="plass in parkering">
        <div>
          <form :action=`/api/reserver` method="post">
            <h5>Beskrivelse</h5>
            <p>{{plass.beskrivelse}}</p>
            <h5>Koordinater</h5>
            <p>{{plass.breddegrad}} bredde, {{plass.lengdegrad}} lengde</p>
            <div>
              <p>Pris pr time: {{plass.pris}}kr</p>
              <div>
                <label>Fra:</label>
                <input type="datetime-local" name="ledig.fra" v-model="ledig.fra" required>
              </div>
              <div>
                <label>Til:</label>
                <input type="datetime-local" name="ledig.til" v-model="ledig.til" required>
              </div>
              <p>Antall plasser igjen: {{plass.antallPlasser}}</p>
              <p>Antall handicapplasser igjen: {{plass.antallHandicap}}</p>
              <div v-if="plass.antallHandicap != 0">
                <label>Handicap:</label>
                <input type="checkbox" name="ledig.handicap" v-model="plass.handicap">
              </div>
              <input type="hidden" :value="ledig.parkerinsgplassId = plass.id" name="ledig.parkerinsgplassId"  required>
              <input type="hidden" :value="ledig.brukerId = bruker.id" name="bruker.id" required v-if="bruker != null">
              <p>Utleier: {{visEierNavn(plass.eierId)}}</p>
              <input id="reserver" type="submit" value="Reserver" v-if="bruker != null">
            </div>
          </form>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
Vue.component("index", {
  template: "#index",
  data: () => ({
    BP: [],
    parkering: [],
    brukere: [],
    bruker: null,
    leiUt: {
      pris: null,
      antall: null,
      antallHandicap: null,
      lengdegrad: null,
      breddegrad: null,
      beskrivelse: null,
      brukerId: null,
      fra: null,
      til: null
    },
    ledig: {
      fra: null,
      til: null,
      handicap: false,
      parkerinsgplassId: null,
      brukerId: null
    }
  }),
  created() {
    fetch(`/api/`)
        .then(res => res.json())
        .then(res => this.BP = res)
        .then(res => this.bruker = this.BP[0][0])
        .then(res => this.parkering = this.BP[1])
        .then(res => this.brukere = this.BP[2])
        .catch(() => alert("Error kunne ikke skaffe lista"));
  },
  methods:{
    visEierNavn:function(plassEierId){
      var navn = "";
      if(this.brukere.length === 0){
        return 0;
      }

      for(let j=0; j<this.brukere.length; j++){
        if(this.brukere[j].id === plassEierId) {
          navn = this.brukere[j].navn;
        }
      }
      return navn;
    }
  }
});
</script>

<style scoped>
  body{
    box-sizing: border-box;
    background-color: whitesmoke;
  }
  #index-app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-areas:
      "header header"
      "leiUt ledige";
    margin: 5vw;
  }

  #header {
    grid-area: header;
    background-color: yellowgreen;
    margin: 2vw 2vw 0;
    border-radius: 1rem;
  }
  header > h1 {
    margin: 2vw;
    grid-area: overskrift;
  }

  header > div{
    grid-area: innlogget;
    margin: 2vw;
    display: inline-flex;
    gap: 1rem;
  }

  header > div > div > a, header > div > div > button {
    padding: 0.5rem;
    margin: 0 0.5rem;
  }

  .leiUt {
    grid-area: leiUt;
    background-color: grey;
    margin: 2vw;
    border-radius: 1rem;
    height: fit-content;
  }

  .leiUt > form {
    margin: 2vw;
    display: flex;
    flex-direction: column;
  }

  .leiUt > form > div {
    margin-top: 0.75vw;
    margin-bottom: 0.75vw;
  }

  .leiUt > form > div > label {
    float: left;
  }

  .leiUt > form > div > input {
    width: 60%;
    height: 4vh;
    float: right;
  }

  .leiUt > form > input {
    width: 30%;
    margin: 0 auto;
  }

  #ledige {
    grid-area: ledige;
  }

  .articleLedige {
    border-radius: 1rem;
    margin: 2vw;
    min-height: 15vw;
    min-width: 88%;
    padding: 0.5vw;
    background-color: grey;
    display: inline-block;
  }

  .articleLedige > img {
    max-width: 30%;
    border-radius: 1rem;
    float: left;
  }

  .articleLedige > div > form > p {
    display: inline-block;
    margin: 0.5vw;
  }

  .articleLedige > div > form > div {
    display: flex;
    flex-direction: column;
  }

  #reserver {
    width: 30%;
    margin: 0 auto;
  }

  .tilDiv {
    grid-area: knapp;
    justify-content: center;
    align-items: center;
  }
</style>
