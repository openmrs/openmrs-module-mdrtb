
//******************************
// Adds a new drug entry row immediately before the sectionId element of a table
//******************************
function addDrug(sectionId, drug, drugOptions, unitOptions, suffix) {
	$drugSelector = $j('<select name="drug:' + suffix + '">' + drugOptions + '</select>').val(drug.drugId);
	$doseSelector = $j('<input type="text" size="6" name="dose:' + suffix + '"/>').val(drug.dose);
	$unitSelector = $j('<select name="unit:' + suffix + '">' + unitOptions + '</select>').val(drug.units);
	$frequencySelector = $j('<input type="text" name="frequency:' + suffix + '"/>').val(drug.frequency);
	$endDateSelector = $j('<input type="text" size="10" tabIndex="-1" name="autoExpireDate:' + suffix + '" onFocus=\"showCalendar(this)\"/>').val(drug.autoExpireDate);
	$instructionsSelector = $j('<input type="text" name="instructions:' + suffix + '"/>').val(drug.instructions);
	$removeButton = $j('<input type="button" value=" X " onclick="removeElement(\'addDrugRow' + drug.drugId + '-' + suffix + '\');" />');
	
	$newRow = $j('<tr id="addDrugRow' + drug.drugId + '-' + suffix + '">');
	$newRow.append($j('<td>').html($drugSelector));
	$newRow.append($j('<td>').html($doseSelector).append($unitSelector));
	$newRow.append($j('<td>').html($frequencySelector));
	$newRow.append($j('<td>').html(drug.startDate));
	$newRow.append($j('<td>').html($endDateSelector));
	$newRow.append($j('<td>').html($instructionsSelector));
	$newRow.append($j('<td>').html($removeButton));
	
	$j('#'+sectionId).before($newRow);
}

// Removes the element with the passed id from the DOM
function removeElement(id) {
	$j('#'+id).remove();
}

// Empty Function
function change() {}