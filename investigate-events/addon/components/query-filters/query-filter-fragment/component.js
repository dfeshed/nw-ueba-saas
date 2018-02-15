import Component from 'ember-component';
import EmberObject from 'ember-object';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import set from 'ember-metal/set';
import { isEmpty } from 'ember-utils';
import computed, { equal, notEmpty } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { dirtyQueryToggle, validateIndividualQuery } from 'investigate-events/actions/query-validation-creators';

const quoteValue = (value) => {

  if (value.slice(0, 1) != '\'' && value.slice(0, 1) != '"') {
    value = `'${value}`;
  } else if (value.slice(0, 1) === '"') {
    value = `'${value.slice(1, -1)}`;
  }

  if (value.slice(-1) != '\'' && value.slice(-1) != '"') {
    value = `${value}'`;
  } else if (value.slice(-1) === '"') {
    value = `${value.slice(1, -1)}'`;
  }

  return value;
};

const isFloat = (value) => {
  return value.includes('.') && (value - value === 0);
};

const isIPv4 = (value) => {
  return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(value);
};

const isIPv6 = (value) => {
  return /^((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$/g.test(value);
};

const isInt = (value) => {
  return !value.includes('.') && (value - value === 0);
};

const dispatchToActions = {
  dirtyQueryToggle,
  validateIndividualQuery
};

const QueryFragmentComponent = Component.extend({
  i18n: service(),

  classNames: ['rsa-query-fragment'],

  classNameBindings: [
    'editActive', 'selected', 'empty',
    'typing', 'prevIsEditing', 'isExpensive',
    'queryFragmentInvalid', 'metaIndex'
  ],

  type: 'meta',

  meta: null,

  operator: null,

  value: null,

  filterIndex: 0,

  editActive: false,

  selected: false,

  metaFormat: null,

  isExpensive: false,

  apiMetaMessage: null,

  wasValidated: false,

  saved: false,

  validateWithServer: true,

  @equal('type', 'meta') onMeta: false,
  @equal('type', 'operator') onOperator: false,
  @equal('type', 'value') onValue: false,

  @notEmpty('meta') withMeta: false,
  @notEmpty('operator') withOperator: false,
  @notEmpty('value') withValue: false,

  @computed('type')
  dropdownClass(type) {
    let dropdownClass = 'rsa-query-fragment-dropdown';

    if (type === 'value') {
      dropdownClass = `${dropdownClass} without-options`;
    }

    return dropdownClass;
  },

  @computed('withMeta', 'withOperator', 'withValue', 'complexFilter')
  empty(withMeta, withOperator, withValue, complexFilter) {
    if (isEmpty(complexFilter)) {
      return !withMeta && !withOperator && !withValue;
    } else {
      return false;
    }
  },

  @computed('metaFormat', 'metaIndex', 'metaOptions', 'meta')
  operatorOptions(metaFormat, metaIndex, metaOptions, meta) {
    // const efficientIndex = metaIndex === 'value';
    const eq = { displayName: '=', isExpensive: false };
    const notEq = { displayName: '!=', isExpensive: false };
    const lt = { displayName: '<', isExpensive: false };
    const lte = { displayName: '<=', isExpensive: false };
    const gt = { displayName: '>', isExpensive: false };
    const gte = { displayName: '>=', isExpensive: false };
    const exists = { displayName: 'exists', isExpensive: false };
    const notExists = { displayName: '!exists', isExpensive: false };
    const contains = { displayName: 'contains', isExpensive: false };
    const begins = { displayName: 'begins', isExpensive: false };
    const ends = { displayName: 'ends', isExpensive: false };

    if (isEmpty(metaFormat) && !isEmpty(metaOptions) && !isEmpty(meta)) {
      metaFormat = metaOptions.findBy('metaName', meta.trim()).format;
    }

    if (metaFormat === 'Text') {
      return [ eq, notEq, exists, notExists, contains, begins, ends ];
    } else if (metaFormat === 'IPv4' || metaFormat === 'IPv6') {
      return [ eq, notEq, exists, notExists ];
    } else {
      return [ eq, notEq, lt, lte, gt, gte, exists, notExists ];
    }
  },

  @computed('selected', 'filterList', 'deleteFilter', 'i18n', 'executeQuery')
  contextItems(selected, filterList, deleteFilter, i18n, executeQuery) {
    if (selected) {
      return [{
        label: i18n.t('queryBuilder.querySelected'),
        action() {
          executeQuery(filterList.filterBy('selected', true));
        }
      }, {
        label: i18n.t('queryBuilder.querySelectedNewTab'),
        action() {
          executeQuery(filterList.filterBy('selected', true), true);
        }
      }, {
        label: i18n.t('queryBuilder.delete'),
        action() {
          deleteFilter(filterList.filterBy('selected', true));
          executeQuery(filterList);
        }
      }];
    }
  },

  @computed('filterRecord', 'filterList.lastObject')
  isLastInList(filter, lastFilter) {
    return filter === lastFilter;
  },

  @computed('meta', 'operator', 'value', 'complexFilter')
  filter(meta, operator, value, complexFilter) {
    if (complexFilter) {
      return complexFilter;
    } else {
      return `${meta || ''} ${operator || ''} ${value || ''}`.trim();
    }
  },

  @computed('metaOptions', 'metaOptions.length')
  filteredMetaOptions(metaOptions) {
    if (!isEmpty(metaOptions)) {
      if (metaOptions.asMutable) {
        metaOptions = metaOptions.asMutable();
      }

      return metaOptions.filter((option) => {
        const keyIndexes = ['none', 'key', 'value'];
        const keyIndexType = option.flags & '0xF';

        const withIndex = keyIndexes[keyIndexType - 1] != 'none';
        const isTime = option.metaName === 'time';
        const isSessionId = option.metaName === 'sessionid';

        return withIndex || isTime || isSessionId;
      });
    }
  },

  @computed('filteredMetaOptions', 'filteredMetaOptions.length')
  sortedMetaOptions(metaOptions) {
    if (!isEmpty(metaOptions)) {
      if (metaOptions.asMutable) {
        metaOptions = metaOptions.asMutable();
      }

      return metaOptions.sort((a, b) => {
        a = a.metaName.replace(' ', '').replace('.', '').toLowerCase();
        b = b.metaName.replace(' ', '').replace('.', '').toLowerCase();

        if (a > b) {
          return 1;
        } else {
          return -1;
        }
      });
    }
  },

  @computed('type', 'sortedMetaOptions', 'operatorOptions', 'complexFilter', 'sortedMetaOptions.length', 'valueOptions', 'valueOptions.length', 'operatorOptions.length')
  options(type, sortedMetaOptions, operatorOptions, complexFilter) {
    if (!isEmpty(complexFilter)) {
      return [];
    }

    if (type == 'value') {
      return [];
    } else if (type === 'meta') {
      return sortedMetaOptions;
    } else {
      return operatorOptions;
    }
  },

  @computed('filterList', 'filterIndex', 'filterList.@each.editActive')
  prevIsEditing(filterList, filterIndex) {
    if (!isEmpty(filterList)) {
      const prev = filterList.objectAt(filterIndex - 1);
      return isEmpty(prev) ? false : prev.get('editActive');
    }
  },

  doubleClick() {
    // Do not want to trigger edit if...
    // 1) is saved because ???
    // 2) already editing (can't send editFilter again, or else editing turns off)
    // 3) is complex filter which is not editable
    if (this.get('saved') && !this.get('editActive') && isEmpty(this.get('complexFilter'))) {
      this.send('editFilter');
    }
  },

  click() {
    this._super(...arguments);

    // If clicking on a pill that is being edited
    // then need to reset type based on position.
    // This allows for things like opening the meta
    // dropdown when someone clicks around the pill
    if (this.get('editActive')) {
      this.setTypeFromCursorPostion();
    }
  },

  didInsertElement() {
    this._super(...arguments);
    const input = this.$('input');

    if (!isEmpty(input)) {
      input
        .prop('type', 'text')
        .prop('spellcheck', false);
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);
    if (arguments.length === 0) {
      return;
    }

    const { meta, operator, value, complexFilter } = this;

    // checking for null as this hook is called with null values multiple times.
    let filterText;
    if (isEmpty(complexFilter)) {
      if (meta != null && operator != null && value != null) {
        // api needs a string instead of object
        filterText = `${meta} ${operator} ${value}`;
      }
    } else {
      filterText = complexFilter;
    }

    if (this.get('validateWithServer') && (!this.get('wasValidated') || filterText != this.get('filter'))) {
      if (isEmpty(complexFilter)) {
        this._prevalidation();
      }

      if (!isEmpty(filterText) && !this.get('queryFragmentInvalid')) {
        // TODO: find a way to throttle requests when there are "a very large number" of pills
        // this.send('validateIndividualQuery', filterText, this._validateComplete.bind(this));
        // this.set('wasValidated', true);
      }
    }
  },

  _validateComplete(isValid, apiMetaMessage) {
    if (!isValid) {
      this.set('queryFragmentInvalid', true);
      this.set('wasValidated', true);

      if (!isEmpty(apiMetaMessage) && !isEmpty(apiMetaMessage.message)) {
        this.set('apiMetaMessage', apiMetaMessage.message);
      }
    } else {
      this.send('dirtyQueryToggle');
    }
  },

  _prevalidation() {
    let isValid = true;
    let message = '';
    const value = this.get('value');
    const format = this.get('metaFormat');

    if (isEmpty(value)) {
      return;
    }

    switch (format) {
      case 'TimeT':
        isValid = new Date(value) != 'Invalid Date';
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.time');
        }
        break;
      case 'Text':
        isValid = value.slice(0) != '\'' || value.slice(-1) != '\'';
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.text');
        }
        break;
      case 'IPv4':
        isValid = isIPv4(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.ipv4');
        }
        break;
      case 'IPv6':
        isValid = isIPv6(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.ipv6');
        }
        break;
      case 'UInt8':
        isValid = isInt(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.uint8');
        }
        break;
      case 'UInt16':
        isValid = isInt(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.uint16');
        }
        break;
      case 'UInt32':
        isValid = isInt(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.uint32');
        }
        break;
      case 'Float32':
        isValid = isFloat(value);
        if (!isValid) {
          message = this.get('i18n').t('queryBuilder.validationMessages.float32');
        }
        break;
    }
    this._validateComplete(isValid, { message });
  },

  _insertEmptyFilter(index) {
    const list = this.get('filterList');
    const filter = EmberObject.create({
      meta: null,
      operator: null,
      value: null,
      filter: null,
      filterIndex: index || list.get('length'),
      editActive: true,
      selected: false
    });

    !isEmpty(index) ? list.addObject(filter) : list.insertAt(index, filter);
  },

  _resetFilter() {
    this.setProperties({
      meta: null,
      operator: null,
      value: null,
      selected: null,
      saved: false
    });
  },

  setTypeFromCursorPostion() {
    const input = this.$('input');

    const {
      meta, operator, value
    } = this.getProperties(
      'meta', 'operator', 'value'
    );

    let metaLength = 0;
    let operatorLength = 0;
    if (!isEmpty(meta)) {
      metaLength = meta.length;
    }

    if (!isEmpty(operator)) {
      operatorLength = operator.length;
    }

    const updatedCursorPosition = input.get(0).selectionStart;
    const metaAndOperatorLength = metaLength + operatorLength;

    if (this.get('empty')) {
      return;
    }

    if (updatedCursorPosition === 0) {
      this.set('type', 'meta');
    } else if (isEmpty(operator) && updatedCursorPosition < metaLength) {
      this.set('type', 'meta');
    } else if (!isEmpty(operator) && updatedCursorPosition <= metaLength) {
      this.set('type', 'meta');
    } else if (isEmpty(value) && updatedCursorPosition > metaAndOperatorLength) {
      this.set('type', 'value');
    } else if (isEmpty(value) && updatedCursorPosition <= metaAndOperatorLength) {
      this.set('type', 'operator');
    } else if (!isEmpty(value) && updatedCursorPosition <= metaAndOperatorLength + 1) {
      this.set('type', 'operator');
    } else {
      this.set('type', 'value');
    }
  },

  actions: {
    toggleSelect() {
      if (!this.get('editActive')) {
        this.toggleProperty('selected');
      }
    },

    onblur() {
      this.setKeyboardPriority(0);

      const inputVal = this.$('input').val();

      if (!isEmpty(this.get('meta')) && !isEmpty(this.get('operator'))) {
        if (inputVal.length > this.get('filter').length) {
          let updatedValue = inputVal.replace(this.get('meta'), '').replace(this.get('operator'), '').trim();

          const isExistsOperator = inputVal.includes('exists');
          if (this.get('metaFormat') === 'Text' && !isExistsOperator) {
            updatedValue = quoteValue(updatedValue);
          }

          this.set('value', updatedValue);
          this.set('editActive', false);
          this.set('saved', true);

          this._insertEmptyFilter(-1);
        }
      }

      run.next(() => {
        const input = this.$('input');

        if (isEmpty(input)) {
          return;
        }

        if (!this.get('isLastInList')) {
          const px = 8;
          const valLength = input.val().length;
          const newInputWidth = valLength * px;
          input.width(newInputWidth);
        }

      });
    },

    onfocus(select) {
      this.setKeyboardPriority(1);

      const options = this.get('options');
      if (!isEmpty(options)) {
        set(select, 'results', options);
      }

      if (this.get('saved') && ['exists', '!exists'].includes(this.get('operator'))) {
        this.set('type', 'operator');
      } else if (this.get('saved')) {
        this.set('type', 'value');
      }

      const list = this.get('filterList');
      const prunedList = list
        .without(list.get('lastObject'))
        .without(this.get('filterRecord'));

      prunedList.setEach('selected', false);

      this.$('input')
        .prop('type', 'text')
        .prop('spellcheck', false);

      select.actions.open();

      run.next(() => {
        prunedList.forEach((pill) => {
          if (isEmpty(pill.get('meta')) && isEmpty(pill.get('operator')) && isEmpty(pill.get('value'))) {
            this.deleteFilter(pill);
          } else if (!isEmpty(pill.get('meta')) && !isEmpty(pill.get('operator')) && !isEmpty(pill.get('value'))) {
            pill.set('editActive', false);
          }
        });
      });
    },

    // User takes action via keyboard
    parseInput(select, event) {
      const input = this.$('input');
      const inputVal = this.$('input').val();
      const cursorPosition = input.get(0).selectionStart;
      const {
        filter, filterRecord, filterList, type, filterIndex,
        meta, operator, value, saved
      } = this.getProperties(
        'filter', 'filterRecord', 'type', 'filterList', 'filterIndex',
        'meta', 'operator', 'value', 'saved'
      );

      if (isEmpty(input.get(0))) {
        return;
      }

      if (isEmpty(inputVal)) {
        this.setProperties({
          storedMeta: meta,
          storedOperator: operator,
          storedValue: value,
          type: 'meta',
          meta: null,
          operator: null,
          value: null,
          typing: false
        });

        if (!this.get('isLastInList')) {
          input.width(8);
        }
      } else {
        this.set('typing', true);

        if (!this.get('isLastInList')) {
          input.width((inputVal.length + 1) * 8);
        }
      }

      const pressedSpace = event.keyCode === 32 || event.which === 32 || event.code === 'Space';
      const pressedEnter = event.keyCode === 13 || event.which === 13 || event.code === 'Enter';
      const pressedEscape = event.keyCode === 27 || event.which === 27 || event.code === 'Escape';
      const pressedLeft = event.keyCode === 37 || event.which === 37 || event.code === 'ArrowLeft';
      const pressedRight = event.keyCode === 39 || event.which === 39 || event.code === 'ArrowRight';
      const pressedBackspace = event.keyCode === 8 || event.which === 8 || event.code === 'Backspace';
      const pressedDelete = event.keyCode === 46 || event.which === 46 || event.code === 'Delete';

      if (pressedLeft) {
        if (cursorPosition === 0 && isEmpty(inputVal)) {
          if (filterIndex !== 0) {
            select.actions.close();
          }
        }
      }

      if (pressedEscape) {
        select.actions.close();
        input.blur();

        if (!saved) {
          this.set('type', 'meta');

          if (filterRecord != filterList.get('lastObject')) {
            filterList.removeObject(filterRecord);
          }
        } else {
          this.setProperties({
            type: 'value',
            editActive: false
          });

          if (!isEmpty(this.get('storedMeta')) && !isEmpty(this.get('storedOperator')) && !isEmpty(this.get('storedValue'))) {
            this.setProperties({
              meta: this.get('storedMeta'),
              operator: this.get('storedOperator'),
              value: this.get('storedValue')
            });
          }
        }
      }

      if (pressedSpace) {
        if (type != 'value') {
          if (select.results.length === 1) {
            select.actions.select(select.results[0]);

            if (type === 'meta') {
              this.set('type', 'operator');
            } else if (type === 'operator') {
              this.set('type', 'value');
            }
          } else {
            const match = select.results.find((result) => {
              if (type === 'meta') {
                return result.metaName === select.searchText.toLowerCase();
              } else {
                const valueToMatch = select.searchText.slice(filter.length, select.searchText.length);
                return result.displayName === valueToMatch;
              }
            });

            if (!isEmpty(match)) {
              select.actions.select(match);
            }
          }

          run.next(() => {
            if (!isEmpty(input)) {
              const position = inputVal.length + 1;
              input.val(`${inputVal} `);
              input.get(0).setSelectionRange(position, position);
            }
          });
        }
      }

      if (pressedEnter) {
        if (isEmpty(select.highlighted) && isEmpty(inputVal) && (filterRecord === filterList.get('lastObject'))) {
          return this.executeQuery([]);
        }

        if (!isEmpty(inputVal)) {
          if (this.get('saved') && (filter === inputVal)) {
            this.set('editActive', false);
          } else {
            let updatedValue;

            const updatedMeta = this.get('metaOptions').find((option) => {
              const matchesMetaName = inputVal.trim().includes(option.metaName) && inputVal.charAt(option.metaName.length) != '.' && !inputVal.charAt(option.metaName.length).match(/\w/);
              const inMetaPosition = inputVal.trim().slice(0, option.metaName.length) === option.metaName;
              return matchesMetaName && inMetaPosition;
            });

            if (updatedMeta) {
              const keyIndexes = ['none', 'key', 'value'];
              const keyIndexType = updatedMeta.flags & '0xF';

              this.setProperties({
                meta: updatedMeta.metaName,
                metaFormat: updatedMeta.format,
                metaIndex: keyIndexes[keyIndexType - 1]
              });
            }

            const updatedOperator = this.get('operatorOptions').find((option) => {
              if (inputVal.includes('!exists')) {
                return option.displayName === '!exists';
              } else if (inputVal.includes('<=')) {
                return option.displayName === '<=';
              } else if (inputVal.includes('>=')) {
                return option.displayName === '>=';
              } else if (inputVal.includes('!=')) {
                return option.displayName === '!=';
              } else {
                return inputVal.trim().includes(option.displayName);
              }
            });

            if (updatedOperator) {
              const operatorIndex = inputVal.indexOf(updatedOperator.displayName);
              const textLength = inputVal.length;
              updatedValue = inputVal.slice(operatorIndex + updatedOperator.displayName.length, textLength).trim();
              this.set('operator', updatedOperator.displayName);
              this.set('isExpensive', updatedOperator.isExpensive);

              if (['exists', '!exists'].includes(updatedOperator.displayName)) {
                this.set('saved', true);
              }
            }

            const isExistsOperator = inputVal.includes('exists');
            if (!isEmpty(updatedMeta) && !isEmpty(updatedOperator) && (!isEmpty(updatedValue) || isExistsOperator)) {
              if (this.get('metaFormat') === 'Text' && !isExistsOperator) {
                updatedValue = quoteValue(updatedValue);
              }

              // set query validation properties to default if editing
              // this is required apart from the other api call made in didInsertAttrs
              // as user can edit a invalid pill
              this.setProperties({
                value: updatedValue,
                editActive: false,
                wasValidated: true,
                queryFragmentInvalid: false,
                apiMetaMessage: null,
                saved: true
              });

              this._prevalidation();
              if (this.get('validateWithServer') && !this.get('queryFragmentInvalid')) {
                this.send('validateIndividualQuery', this.get('filter'), this._validateComplete.bind(this));
              }

              if (this.get('isLastInList')) {
                this._insertEmptyFilter(filterList.indexOf(filterRecord) + 1);
              }

              run.next(() => {
                const el = this.$();
                if (!isEmpty(el)) {
                  el.next('.rsa-query-fragment').find('input').focus();
                }
              });
            }
          }
        }
      }

      if (pressedLeft || pressedRight || pressedDelete || pressedBackspace) {
        run.next(() => {
          this.setTypeFromCursorPostion();
        });
      }
    },

    lookup(searchTerm) {
      const {
        options, onMeta, meta, operator,
        value, onOperator
      } = this.getProperties(
        'options', 'onOperator', 'meta', 'operator',
        'value', 'onMeta'
      );

      if (isEmpty(options)) {
        return;
      }

      if (isEmpty(searchTerm)) {
        return options;
      }

      searchTerm = searchTerm.toLowerCase();

      return options.filter((option) => {
        if (onMeta) {
          searchTerm = searchTerm.replace(operator, '').replace(value, '').trim();
          const matchesMetaName = option.metaName.includes(searchTerm);
          const matchesDisplayName = option.displayName.includes(searchTerm);
          return matchesMetaName || matchesDisplayName;
        } else if (onOperator) {
          searchTerm = searchTerm.replace(meta, '').replace(value, '').trim();

          if (isEmpty(searchTerm)) {
            return true;
          } else {
            return option.displayName.slice(0, searchTerm.length) === searchTerm;
          }
        }
      });
    },

    // User makes selection via typeahead
    updateFilter(selection, select) {
      const {
        filterList, type, filterRecord
      } = this.getProperties(
        'filterList', 'type', 'filterRecord'
      );
      const input = this.$('input');
      const inputVal = input.val();

      // select on Enter key press when only one dropdown option
      if (isEmpty(selection) && select.results.length === 1) {
        selection = select.results[0];
      } else if (isEmpty(selection)) {
        return;
      }

      if (type === 'meta') {
        if (!isEmpty(inputVal) && inputVal.indexOf(this.get('value')) === -1) {
          this.set('value', null);
        }

        if (!isEmpty(inputVal) && inputVal.indexOf(this.get('operator')) === -1) {
          this.set('operator', null);
        }

        const keyIndexes = ['none', 'key', 'value'];
        const keyIndexType = selection.flags & '0xF';

        this.setProperties({
          meta: selection.metaName,
          metaFormat: selection.format,
          metaIndex: keyIndexes[keyIndexType - 1],
          type: 'operator'
        });

        if (this.get('saved') && ['exists', '!exists'].includes(this.get('operator'))) {
          this.set('editActive', false);
        } else if (this.get('saved') && !isEmpty(this.get('operator')) && !isEmpty(this.get('value'))) {
          this.set('editActive', false);
        } else {
          this.set('editActive', true);
        }

        run.next(() => {
          const input = this.$('input');

          if (input && !isEmpty(input.get(0))) {
            if (!this.get('isLastInList')) {
              input.width((input.length + 1) * 8);
            }

            if (!this.get('editActive')) {
              this.$().closest('.rsa-query-meta').find('.rsa-query-fragment:last-of-type input').focus();
            } else {
              const position = this.get('meta').length;
              input[0].setSelectionRange(position, position);
              input.focus();
            }
          }
        });
      } else if (type === 'operator') {
        if (inputVal && inputVal.indexOf(this.get('value')) === -1) {
          this.set('value', null);
        }

        this.setProperties({
          isExpensive: false,
          operator: selection.displayName
        });

        if (this.get('saved') && ['exists', '!exists'].includes(this.get('operator'))) {
          this.set('editActive', false);
        } else if (this.get('saved') && !isEmpty(this.get('value'))) {
          this.set('editActive', false);
        } else {
          this.set('editActive', true);
        }

        if (!this.get('editActive')) {
          this.$().closest('.rsa-query-meta').find('.rsa-query-fragment:last-of-type input').focus();
        } else {
          const position = `${this.get('meta')} ${this.get('operator')}`.length;
          input[0].setSelectionRange(position, position);
          this.$('input').focus();
        }

        if (['exists', '!exists'].includes(selection.displayName)) {
          this.setProperties({
            value: null,
            saved: true,
            editActive: false
          });

          if (this.get('isLastInList')) {
            this._insertEmptyFilter(filterList.indexOf(filterRecord) + 1);

            run.next(() => {
              this.$().closest('.rsa-query-meta').find('.rsa-query-fragment:last-of-type input').focus();
            });

          }
        } else {
          this.set('type', 'value');
        }
      }

      run.next(() => {
        if (!this.get('isLastInList')) {
          const input = this.$('input');

          if (!input || isEmpty(input[0])) {
            return;
          }

          input.width((input.val().length + 1) * 8);
        }
      });
    },

    editFilter() {
      this.toggleProperty('editActive');

      run.next(() => {
        const input = this.$('input');

        // Protect against possible race condition, largely in tests
        // where input isn't there just yet
        if (!input || isEmpty(input[0])) {
          return;
        }

        input
          .prop('type', 'text')
          .prop('spellcheck', false)
          .focus();

        if (!this.get('isLastInList')) {
          const px = 8;
          const valLength = input.val().length;
          const newInputWidth = valLength * px;

          input.width(newInputWidth);
        }
      });
    },

    deleteFilter() {
      this.deleteFilter(this.get('filterRecord'));

      // trigger focus on empty query fragment
      // so one fragment disappears, and user is placed in
      // edit mode for brand new fragment
      this.$().closest('.rsa-query-meta').find('.rsa-query-fragment:last-of-type input').focus();
    }
  }

});

export default connect(null, dispatchToActions)(QueryFragmentComponent);
