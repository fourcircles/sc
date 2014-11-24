
i18n.init({
    detectLngQS: 'locale',
    resGetPath: 'locales/synergy/__lng__/synergy.json',
    fallbackOnEmpty: true
});

function getLocalizedString(key) {
    return i18n.t(key);
}
