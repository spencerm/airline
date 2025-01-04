let showFilterRow = false;
function toggleLinksTableFilterRow() {
    if (showFilterRow) {
        $("#linksTableFilterHeader").hide();
        showFilterRow = false;
    } else {
        $("#linksTableFilterHeader").show();
        showFilterRow = true;
    }
}

let linksColumnFilter = {}
function updateLinksColumnFilterOptions(values) {
    Object.entries(values).forEach(([column, rows]) => {
        let selectElement = $('<select>', {
            multiple: "multiple",
            style: "width: 100%; background: transparent"
        });
        selectElement.append($('<option>', {
            value: "",
            style: "font-weight: bold",
            text: "-- Show All --",
        }));

        // Render Country-Airport
        if (column === "fromAirportCode" || column === "toAirportCode") {
            Object.entries(rows).forEach(([countryCode, airportRow]) => {
                const countryGroup = $('<option>', {
                    value: countryCode,
                    style: `background: left no-repeat url(${getCountryFlagUrl(countryCode)}); padding-left: 30px; font-weight: bold`,
                    text: countryCode
                });
                selectElement.append(countryGroup);

                Object.entries(airportRow).forEach(([airportCode, airportCity]) => {
                    const airport = $('<option>', { value: countryCode + '-' + airportCode, text: airportCity });
                    selectElement.append(airport);
                });
            });

            $(selectElement).on("change", function(event) {
                const countryColumn = column.replace("Airport", "Country");
                linksColumnFilter[countryColumn] = [];
                linksColumnFilter[column] = [];

                for (let option of this.selectedOptions) {
                    if (option.value === "") {
                        // Show all
                        linksColumnFilter[countryColumn] = [];
                        linksColumnFilter[column] = [];
                        break;
                    }

                    [countryCode, airportCode] = option.value.split("-");
                    if (countryCode === undefined && airportCode === undefined) {
                        return;
                    }

                    if (airportCode === undefined) {
                        // Selected a country group
                        linksColumnFilter[countryColumn].push(countryCode);
                    } else {
                        linksColumnFilter[column].push(airportCode);
                    }
                }

                var selectedSortHeader = $('#linksTableSortHeader .cell.selected')
                updateLinksTable(selectedSortHeader.data('sort-property'), selectedSortHeader.data('sort-order'))
            });
        } else {
            Object.entries(rows).forEach(([key, value]) => {
                const airport = $('<option>', { value: key, text: value });
                selectElement.append(airport);
            });

            $(selectElement).on("change", function(event) {
                linksColumnFilter[column] = [];

                for (let option of this.selectedOptions) {
                    if (option.value === "") {
                        // Show all
                        linksColumnFilter[column] = [];
                        break;
                    }

                    linksColumnFilter[column].push(option.value);
                }

                var selectedSortHeader = $('#linksTableSortHeader .cell.selected')
                updateLinksTable(selectedSortHeader.data('sort-property'), selectedSortHeader.data('sort-order'))
            });
        }

        const filterDiv = $('#linksTableFilterHeader').find(`[data-filter-property='${column}']`);
        filterDiv.empty();
        filterDiv.append(selectElement);
    });
}

// Modal method below

function toggleTableColumnFilterModal() {
    const columnValues = aggregateColumnValues($("#linksTable"));
    //updateTableColumnFilterSelect(columnValues)
    updateTableColumnFilterForm(columnValues);

    if (!$("#tableColumnFilterModal").is(":visible")) {
        $('#tableColumnFilterModal').fadeIn(500)
    } else {
        closeModal($('#tableColumnFilterModal'))
    }
}

function aggregateColumnValues(table) {
    let aggregatedValues = {
        airports: {
            from: {},
            to: {},
        },
        models: {},
    };

    // Loop through each row in the table
    table.find('.table-row').each(function () {
        // Find all cells in the current row

        const data = $(this).data();
        console.log("aggregateColumnValues DATA", data);

        if (data) {
            aggregatedValues.airports.from[data.fromCountryCode] = aggregatedValues.airports.from[data.fromCountryCode] || {};
            aggregatedValues.airports.to[data.toCountryCode] = aggregatedValues.airports.to[data.toCountryCode] || {};

            aggregatedValues.airports.from[data.fromCountryCode][data.fromAirportId] = data.fromAirportCity + ' (' + data.fromAirportCode + ')';
            aggregatedValues.airports.to[data.toCountryCode][data.toAirportId] = data.toAirportCity + ' (' + data.toAirportCode + ')';
            aggregatedValues.models[data.modelId] = data.modelName;
        }
    });

    /*
    return Object.entries(aggregatedValues)
        .sort((a, b) => {
            // Sort by count (descending), then alphabetically (ascending)
            if (b[1] === a[1]) {
                return a[0].localeCompare(b[0]); // Alphabetical comparison
            }
            return b[1] - a[1]; // Count comparison
        })
        .map(entry => ({ value: entry[0], count: entry[1] }));
     */
    return aggregatedValues;
}

function updateTableColumnFilterForm(values) {
    const countriesFromForm = $('#tableColumnFilterCountriesFrom');
    const airportsFromForm = $('#tableColumnFilterAirportsFrom');
    const countriesToForm = $('#tableColumnFilterCountriesTo');
    const airportsToForm = $('#tableColumnFilterAirportsTo');
    const modelsForm = $('#tableColumnFilterModels');
    countriesFromForm.innerHTML = "";
    airportsFromForm.innerHTML = "";
    countriesToForm.innerHTML = "";
    airportsToForm.innerHTML = "";
    modelsForm.innerHTML = "";

    console.log("updateTableColumnFilterForm", values);

    Object.entries(values.airports.from).forEach(([countryCode, airportInfo]) => {
        const flexDiv = $('<div>', { class: 'flex-align-center' }) // Create the parent div
            .append(
                $('<input>', {
                    type: 'checkbox',
                    name: 'countries-from-'+countryCode,
                    //class: 'toggleUtilizationRateBox',
                    //onclick: "toggleUtilizationRate($('#modelConfigurationModal'), $(this))"
                })
            )
            .append(
                getCountryFlagImg(countryCode)
            );
        countriesFromForm.append(flexDiv);

        Object.entries(airportInfo).forEach(([airportId, airportName]) => {
            const flexDiv = $('<div>', { class: 'flex-align-center' }) // Create the parent div
                .append(
                    $('<input>', {
                        type: 'checkbox',
                        id: 'airports-from-'+airportId,
                        name: 'airports-from-'+airportId,
                        //class: 'toggleUtilizationRateBox',
                        //onclick: "toggleUtilizationRate($('#modelConfigurationModal'), $(this))"
                    })
                )
                .append(
                    $('<label>', {
                        for: 'airports-from-'+airportId,
                        text: airportName,
                    })
                );
            airportsFromForm.append(flexDiv);
        })
    });

    Object.entries(values.airports.to).forEach(([countryCode, airportInfo]) => {
        const flexDiv = $('<div>', { class: 'flex-align-center' }) // Create the parent div
            .append(
                $('<input>', {
                    type: 'checkbox',
                    name: 'countries-to-'+countryCode,
                    //class: 'toggleUtilizationRateBox',
                    //onclick: "toggleUtilizationRate($('#modelConfigurationModal'), $(this))"
                })
            )
            .append(
                getCountryFlagImg(countryCode)
            );
        countriesToForm.append(flexDiv)

        Object.entries(airportInfo).forEach(([airportId, airportName]) => {
            const flexDiv = $('<div>', { class: 'flex-align-center' }) // Create the parent div
                .append(
                    $('<input>', {
                        type: 'checkbox',
                        id: 'airports-to-'+airportId,
                        name: 'airports-to-'+airportId,
                        //class: 'toggleUtilizationRateBox',
                        //onclick: "toggleUtilizationRate($('#modelConfigurationModal'), $(this))"
                    })
                )
                .append(
                    $('<label>', {
                        for: 'airports-to-'+airportId,
                        text: airportName,
                    })
                );
            airportsToForm.append(flexDiv);
        })
    });

    Object.entries(values.models).forEach(([modelId, modelName]) => {
        const flexDiv = $('<div>', { class: 'flex-align-center' }) // Create the parent div
            .append(
                $('<input>', {
                    type: 'checkbox',
                    name: 'model-'+modelId,
                    //class: 'toggleUtilizationRateBox',
                    //onclick: "toggleUtilizationRate($('#modelConfigurationModal'), $(this))"
                })
            )
            .append(
                $('<label>', {
                    for: 'model-'+modelId,
                    text: modelName,
                })
                    .append(getAirplaneIconImg(
                        {isReady: true, condition: 1},
                        1,
                        true
                    ))
            );
        modelsForm.append(flexDiv);
    })
}

function updateTableColumnFilterSelect(values) {
    const dropdown = document.getElementById('tableColumnFilterSelect');
    dropdown.innerHTML = "";
    const reset = document.createElement("option");
    reset.value = null;
    reset.textContent = "-- Show All --"
    dropdown.appendChild(reset);

    values.forEach(item => {
        const option = document.createElement("option");
        option.value = item.value; // Set value attribute
        option.textContent = `${item.value} (${item.count})`; // Set display text
        $(option).data('filter-property', property);
        $(option).data('filter-value', item.value);
        dropdown.appendChild(option);
    });
}

function tableColumnFilterSelectValue(event) {
    const selectedOption = $(event.target).find(':selected'); // Get the selected <option>
    const filterProperty = selectedOption.data('filter-property') || null; // Access filter-property
    const filterValue = selectedOption.data('filter-value') || null; // Access filter-value

    var linksTable = $("#linksCanvas #linksTable");
    $(linksTable).data("filter-property", filterProperty);
    $(linksTable).data("filter-value", filterValue);

    updateLinksTable();
}
