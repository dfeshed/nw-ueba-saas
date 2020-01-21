/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 * @public
 */
import Ember from 'ember';
import Route from '@ember/routing/route';
import RSVP from 'rsvp';
import { get } from '@ember/object';
import { inject as service } from '@ember/service';
import { jwt_decode as jwtDecode } from 'ember-cli-jwt-decode';
import { warn } from '@ember/debug';
import computed from 'ember-computed-decorators';

const {
  testing
} = Ember;

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the '/login' route.
 * @public
 */
export default Route.extend({

  

  model() {
    debugger;  
  }
});
