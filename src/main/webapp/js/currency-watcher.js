
function StorageWrapper(storage) {

    var _storage = storage || localStorage;

    /**
     * Returns array of currency pairs from local storage
     */
    this.loadCurrencyPairs = function() {
        var currencyPairsStr = _storage.getItem('currencyPairs');
        var currencyPairs = [];
        try {
            currencyPairs = currencyPairsStr ? JSON.parse(currencyPairsStr) : [];
        } catch (e) {
            console.error(e);
        }
        return currencyPairs;
    };

    /**
     * Adds given currency pair to local storage and returns new array of currency pairs.
     * If local storage already contains given currency pair then such pair will not be added.
     */
    this.addCurrencyPair = function(currencyPair) {
        var currencyPairs = this.loadCurrencyPairs();
        if ($.inArray(currencyPair, currencyPairs) < 0) {
            currencyPairs.push(currencyPair);
            _storage.setItem('currencyPairs', JSON.stringify(currencyPairs));
        } else {
            console.log('Currency pair', currencyPair, 'already is in storage');
        }
        return currencyPairs;
    };

    /**
     * Removes given currency pair from local storage and returns new array of currency pairs.
     * If local storage does not contain given currency pair then array of currency pair will not be changed.
     */
    this.removeCurrencyPair = function(currencyPair) {
        var currencyPairs = this.loadCurrencyPairs();
        if (currencyPairs.length > 0) {
            var i = $.inArray(currencyPair, currencyPairs);
            if (i == currencyPairs.length - 1) {
                currencyPairs.pop();
            } else if (i > 0) {
                currencyPairs = currencyPairs.slice(0, i).concat(currencyPairs.slice(i + 1));
            } else if (i == 0) {
                currencyPairs = currencyPairs.slice(i + 1);
            }
            _storage.setItem('currencyPairs', JSON.stringify(currencyPairs));
        } else {
            console.log('No any currency pair in storage');
        }
        return currencyPairs;
    };
}