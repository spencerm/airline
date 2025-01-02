function toggleTableColumnFilterModal(event, sender) {
    event.stopPropagation();
    const cell = sender.closest('.cell');
    const property = $(cell).data("sort-property");
    const index = $(cell).index() - 1;
    const columnValues = aggregateColumnValues(index);
    updateTableColumnFilterSelect(columnValues, property)

    if (!$("#tableColumnFilterModal").is(":visible")) {
        $('#tableColumnFilterModal').fadeIn(500)
    } else {
        closeModal($('#tableColumnFilterModal'))
    }
}

function aggregateColumnValues(columnIndex) {
    let aggregatedValues = {};

    // Loop through each row in the table
    $("#linksTable").find('.table-row').each(function () {
        // Find all cells in the current row
        const cell = $(this).find('.cell').eq(columnIndex);

        // Get the value from the cell at the specified columnIndex
        const cellValue = cell.text().trim();

        if (cellValue) {
            aggregatedValues[cellValue] = (aggregatedValues[cellValue] || 0) + 1;
        }
    });

    return Object.entries(aggregatedValues)
        .sort((a, b) => {
            // Sort by count (descending), then alphabetically (ascending)
            if (b[1] === a[1]) {
                return a[0].localeCompare(b[0]); // Alphabetical comparison
            }
            return b[1] - a[1]; // Count comparison
        })
        .map(entry => ({ value: entry[0], count: entry[1] }));
}

function updateTableColumnFilterSelect(values, property) {
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
