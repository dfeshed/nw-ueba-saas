import Ember from 'ember';
import layout from '../templates/components/rsa-application-footer';

const { Component } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-application-footer'],

  title: null,

  version: null

});