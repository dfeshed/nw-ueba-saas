import Controller from '@ember/controller';
import EmberObject from '@ember/object';

export default Controller.extend({

  activeTab: 'packets',

  contentSections: [
    EmberObject.create({
      name: 'files',
      label: 'Files'
    }),
    EmberObject.create({
      name: 'hosts',
      label: 'Hosts'
    }),
    EmberObject.create({
      name: 'packets',
      label: 'Packets'
    }),
    EmberObject.create({
      name: 'long',
      label: 'A Much Longer Title'
    })
  ],

  actions: {
    onTabClick(value) {
      if (!this.isDestroyed && !this.isDestroying) {
        this.set('activeTab', value);
      }
    }
  }

});
