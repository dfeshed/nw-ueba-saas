import Ember from "ember";
import config from "./config/environment";

var Router = Ember.Router.extend({
    location: config.locationType
});

Router.map(function() {
    this.route("login");
    this.route("protected", {path: "/do"}, function(){
        this.route("monitor");
        this.route("respond");
        this.route("explore");
        this.route("admin");
        this.route("not-found", {path: "*invalidprotectedpath"});
    });
    this.route("not-found", {path: "*invalidrootpath"});
});

export default Router;
