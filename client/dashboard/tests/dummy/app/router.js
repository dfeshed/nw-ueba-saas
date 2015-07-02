import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
    location: config.locationType
});

Router.map(function () {
    this.route('project');
    this.route('styleguide', function() {
        this.route('style', function() {
            this.route('color');
        });
        this.route('atom', function() {
            this.route('button');
        });
    });
});

export default Router;
