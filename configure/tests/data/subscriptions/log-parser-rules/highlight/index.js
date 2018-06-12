export default {
  subscriptionDestination: '/user/queue/content/parser/highlight',
  requestDestination: '/ws/content/parser/highlight',
  message(/* frame */) {
    return {
      code: 0,
      data: 'May 5 2010 15:55:49 switch : %ACE-4-400000: IDS:1000 IP Option Bad Option List by user admin@test.com ' +
      '<span class="highlight_capture_SourceIPorIP:Port"><span class="highlight_literal_SourceIPorIP:Port"> from ' +
      '</span>10.100.229.59</span>to 224.0.0.22 on<span class="highlight_capture_AnyPort"><span class="highlight_literal_AnyPort"> ' +
      'port </span>12345</span> \\n\\nApr 29 2010 03:15:34 pvg1-ace02: %ACE-3-251008: Health probe failed for server ' +
      '218.83.175.75:81, connectivity error: server open timeout (no SYN ACK)<span class="highlight_capture_AnyDomain"> ' +
      '<span class="highlight_literal_AnyDomain"> domain </span>google.com</span> with mac 06-00-00-00-00-00.'
    };
  }
};
