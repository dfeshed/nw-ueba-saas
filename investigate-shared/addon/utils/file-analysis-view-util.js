// Returns string view path or the text view path depending on the file type

const stringViewFileExtensionList = ['pe', 'macho', 'elf'];

const stringView = {
  component: 'endpoint/string-view',
  title: 'investigateShared.endpoint.fileAnalysis.stringsView',
  format: 'string'
};
const textView = {
  component: 'endpoint/text-view',
  title: 'investigateShared.endpoint.fileAnalysis.textView',
  format: 'text'
};

export function componentSelectionForFileType(fileType) {
  return stringViewFileExtensionList.includes(fileType) ? stringView : textView;
}