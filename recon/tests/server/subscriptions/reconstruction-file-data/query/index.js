export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-files',
  requestDestination: '/ws/investigate/reconstruct/session-files',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

// format
// https://github.rsa.lab.emc.com/gist/bashfd/242535e967c24ee2d83aee06c8723cbf
// [{
//   type: '', // session or link
//   extension: '',
//   fileName: '',
//   mimeType: '',

//   id: '', // session only, used for download
//   fileSize: 1234, // session only
//   hashes: [{  // session only
//     type: '',
//     value: ''
//   }],

//   query: '' // link only
//   start: 1234 // link only
//   end: 1234 // link only
// }]

const data = [{
  type: 'session',
  extension: 'docx',
  fileName: 'a_file_name.docx',
  mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  id: '1234125',
  fileSize: 312412,
  hashes: [{
    type: 'md5',
    value: 'f71f80a9cb8e24b06419a895cadd1a47'
  }, {
    type: 'sha1',
    value: '5a39b799a8f63cf4dd774d4ee024715ed25e252a'
  }]
}, {
  type: 'session',
  extension: 'pdf',
  fileName: 'a_pdf_name.pdf',
  mimeType: 'application/pdf',
  id: '554667',
  fileSize: 12341,
  hashes: [{
    type: 'md5',
    value: 'f71f80acccccccb06419a895cadd1a47'
  }, {
    type: 'sha1',
    value: '5a39b799a8f6399999999d4ee024715ed25e252a'
  }]
}];

