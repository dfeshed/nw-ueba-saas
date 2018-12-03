// Returns string view path or the text view path depending on the file type

const stringViewFileExtensionList = ['pe', 'macho', 'elf'];

const stringView = {
  component: 'endpoint/string-view',
  title: 'investigateShared.endpoint.fileAnalysis.stringsView'
};
const textView = {
  component: 'endpoint/text-view',
  title: 'investigateShared.endpoint.fileAnalysis.textView'
};

export function componentSelectionForFileType(fileType) {
  return stringViewFileExtensionList.includes(fileType) ? stringView : textView;
}