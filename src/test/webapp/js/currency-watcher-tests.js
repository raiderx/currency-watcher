
function FakeStorage() {

    var _value = null;

    this.getItem = function () {
        return _value;
    };

    this.setItem = function (k, v) {
        _value = v;
    };
}

QUnit.test('StorageWrapper.loadCurrencyPairs()', function(assert) {

    var storage = new FakeStorage();
    var wrapper = new StorageWrapper(storage);

    storage.setItem(null, null);
    assert.deepEqual(wrapper.loadCurrencyPairs(), []);

    storage.setItem(null, 'zzz');
    assert.deepEqual(wrapper.loadCurrencyPairs(), []);

    storage.setItem(null, '[]');
    assert.deepEqual(wrapper.loadCurrencyPairs(), []);

    storage.setItem(null, JSON.stringify(['USD/RUB', 'EUR/RUB']));
    assert.deepEqual(wrapper.loadCurrencyPairs(), ['USD/RUB', 'EUR/RUB']);

});

QUnit.test('StorageWrapper.addCurrencyPair()', function(assert) {

    var storage = new FakeStorage();
    var wrapper = new StorageWrapper(storage);

    storage.setItem(null, null);
    wrapper.addCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify(['USD/RUB']));

    storage.setItem(null, JSON.stringify(['USD/RUB']));
    wrapper.addCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify(['USD/RUB']));

    storage.setItem(null, JSON.stringify(['USD/RUB']));
    wrapper.addCurrencyPair('EUR/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify(['USD/RUB', 'EUR/RUB']));

});

QUnit.test('StorageWrapper.removeCurrencyPair()', function(assert) {

    var storage = new FakeStorage();
    var wrapper = new StorageWrapper(storage);

    storage.setItem(null, null);
    wrapper.removeCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), null);

    storage.setItem(null, JSON.stringify([]));
    wrapper.removeCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify([]));

    storage.setItem(null, JSON.stringify(['EUR/RUB']));
    wrapper.removeCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify(['EUR/RUB']));

    storage.setItem(null, JSON.stringify(['USD/RUB', 'EUR/RUB']));
    wrapper.removeCurrencyPair('USD/RUB');
    assert.deepEqual(storage.getItem(), JSON.stringify(['EUR/RUB']));

});