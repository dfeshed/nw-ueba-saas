import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'box',

  classNames: ['file-analysis-accordion-list'],

  @computed('data')
  importedDllInfoList(data) {
    const keys = data ? Object.keys(data) : [];
    let totalFunctionCount = 0;

    const importedDllAndFunctionCountList = keys && keys.map(
      (importedDll) => {
        const functionCount = data[importedDll].length;
        totalFunctionCount = totalFunctionCount + functionCount;
        const functionList = [...data[importedDll]];
        return {
          importedDll,
          functionCount,
          functionList
        };
      }
    );

    return {
      importedDlls: {
        length: keys.length,
        totalFunctionCount
      },
      importedDllAndFunctionCountList
    };
  }
});