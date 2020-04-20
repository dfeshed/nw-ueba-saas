import allData from './data';
import _ from 'lodash';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-emails',
  requestDestination: '/ws/investigate/reconstruct/session-emails/stream',

  page(frame, sendMessage) {
    const requestBody = JSON.parse(frame.body);

    /**
     * To simulate reactive streaming, the data is responded in 2 parts. In the first request there will not be a marker,
     * so the first part of the data is sent along with a marker. In the second request the marker is sent back in the
     * request.
     */
    const marker = requestBody.filter.find((f) => f.field === 'marker' && f.value === 'fake-marker');
    const part1 = _.cloneDeep(allData.slice(0, 4));
    const splitEmail = part1[part1.length - 1];
    if (!marker) {

      // Send 3 emails and just a part (first 2000 char) of the 4th email body in the 1st response
      splitEmail.bodyContent = splitEmail.bodyContent.substring(0, 2000);
      sendMessage({
        data: part1,
        meta: {
          'RECON-EMAIL-MESSAGE-SPLIT': true,
          marker: 'fake-marker',
          complete: false
        }
      });

    } else {

      // send the remaining part of the split email body and remaining emails in the 2nd response
      const part2FirstMail = _.cloneDeep(allData.slice(0, 4)[part1.length - 1]);
      part2FirstMail.bodyContent = splitEmail.bodyContent.substring(2000, splitEmail.bodyContent.length);
      const part2 = allData.slice(4, allData.length);
      sendMessage({
        data: [part2FirstMail].concat(part2),
        meta: {
          complete: true
        }
      });
    }

  }
};