import Base from 'simple-auth/authorizers/base';
import config from '../config/environment';

export default Base.extend({
    authorize: function(jqXHR) {
        var csrfKey = config["simple-auth"].csrfLocalstorageKey;
        jqXHR.setRequestHeader('X-CSRF-TOKEN', localStorage.getItem(csrfKey));
    }
});
