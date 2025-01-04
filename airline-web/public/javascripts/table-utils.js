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
