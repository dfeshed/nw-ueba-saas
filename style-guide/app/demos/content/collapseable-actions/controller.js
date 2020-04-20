import Controller from '@ember/controller';
import EmberObject, { computed } from '@ember/object';
import { A } from '@ember/array';

export default Controller.extend({
  showNulls: false,
  dropdownLabel: 'Dropdown Option 1',

  stub() {
    console.log('stub'); // eslint-disable-line no-console
  },

  dropdownList: computed('dropdownLabel', function() {
    return [
      EmberObject.create({
        component: 'dropdown',
        label: this.get('dropdownLabel'),
        icon: 'cog',
        nestedActions: A([{
          label: 'Dropdown Option 1',
          icon: 'cog',
          action: () => this.send('changeDropdownLabel', 'Dropdown Option 1')
        }, {
          label: 'Dropdown Option 2',
          icon: 'cog',
          action: () => this.send('changeDropdownLabel', 'Dropdown Option 2')
        }])
      })
    ];
  }),

  buttonList: computed(function() {
    return [
      EmberObject.create({
        component: 'button',
        label: 'Secondary Button 1',
        icon: 'cog',
        isPrimary: true,
        action: () => this.send('action')
      }),
      EmberObject.create({
        component: 'button-group',
        label: 'Download Pcaps',
        icon: 'cog',
        isPrimary: false,
        action: () => this.send('action'),
        nestedActions: [{
          label: 'Download Files',
          icon: 'cog',
          action: () => this.send('action')
        }, {
          label: 'Download Others',
          icon: 'cog',
          action: () => this.send('action')
        }]
      })
    ];
  }),

  toggleList: computed('showNulls', function() {
    return [
      EmberObject.create({
        component: 'toggle',
        label: 'Show Nulls',
        value: this.get('showNulls'),
        action: () => this.send('toggle')
      }),
      EmberObject.create({
        component: 'toggle',
        label: 'Show Nulls',
        value: this.get('showNulls'),
        action: () => this.send('toggle')
      })
    ];
  }),

  actions: {
    action() {
      console.log('action'); // eslint-disable-line no-console
    },

    toggle() {
      if (!this.isDestroyed && !this.isDestroying) {
        this.toggleProperty('showNulls');
      }
    },

    changeDropdownLabel(newLabel) {
      if (!this.isDestroyed && !this.isDestroying) {
        this.set('dropdownLabel', newLabel);
      }
    }
  }

});
