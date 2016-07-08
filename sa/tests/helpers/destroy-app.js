import Ember from 'ember';

export default function destroyApp(application) {
  localStorage.clear();
  Ember.run(application, 'destroy');
}
