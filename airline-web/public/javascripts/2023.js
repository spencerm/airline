let airlineStrategySaved;
let airlineStrategyPreview;

function loadAirlineStrategy() {
    var airline = getCurrentAirline();
    var url = "airlines/" + airline.id + "/strategy"

    fetch(url)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error, status = ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            airlineStrategySaved = data;
            airlineStrategyPreview = data;
            console.log(airlineStrategyPreview);
            previewAirlineStrategy();
        })
        .catch((error) => {
            console.error("Fetch error:", error);
        });
}

function saveAirlineStrategy() {
    var airline = getCurrentAirline();
    var url = "airlines/" + airline.id + "/strategy"
    console.log('sending: ');
    console.log(airlineStrategyPreview);

    fetch(url, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(airlineStrategyPreview),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error, status = ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            console.log("updated!!");
            console.log(data);
            airlineStrategyPreview = data;
            airlineStrategySaved = data;
            previewAirlineStrategy();
        })
        .catch((error) => {
            console.error("Fetch error:", error);
        });

}

function previewAirlineStrategy(e = null) {

    // update toggles / model
    if (e) {
        airlineStrategyPreview[e.currentTarget.id] = !airlineStrategyPreview[e.currentTarget.id];
        e.currentTarget.classList.toggle('on');
    }

    // update delegates required
    let totalRequired = 0;
    document.querySelectorAll("#airlineStrategy fieldset").forEach(fieldset => {
        let n = 0;
        fieldset.querySelectorAll('.toggle').forEach(toggle => {
            if(airlineStrategyPreview[toggle.id]) {
                toggle.classList.add('on');
                n++;
            } else {
                toggle.classList.remove('on');
            }
        });
        const required = (n * (n - 1)) / 2; 
        fieldset.querySelector('.required').innerText = required;
        totalRequired += required;       
    });
    document.querySelector('#airlineStrategy .totalRequired').innerText = totalRequired;

    // math for delegates


}

/**
 * Setup
 */
window.addEventListener('DOMContentLoaded', (event) => {
    const specialToggles = document.querySelectorAll("#airlineStrategy .toggle");

    specialToggles.forEach(toggle => {
        toggle.addEventListener('click', (event) => {
            previewAirlineStrategy(event);
        });
    });
    loadAirlineStrategy();



});

/**
 * Helper functions
 */
function countTrueValues(obj) {
    let count = 0;
    for (const key in obj) {
        if (obj.hasOwnProperty(key) && obj[key] === true) {
            count++;
        }
    }
    return count;
}