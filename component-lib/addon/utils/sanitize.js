import sanitize from 'sanitize-html';

export function sanitizeHtml(html, params) {
  const policy = {
    allowedTags: [
      'img',
      'p',
      'label',
      'font',
      'a',
      'table',
      'td',
      'th',
      'tr',
      'colgroup',
      'col',
      'thead',
      'tbody',
      'tfoot',
      'noscript',
      'h1',
      'h2',
      'h3',
      'h4',
      'h5',
      'h6',
      'p',
      'i',
      'b',
      'u',
      'strong',
      'em',
      'small',
      'big',
      'pre',
      'code',
      'cite',
      'samp',
      'sub',
      'sup',
      'strike',
      'center',
      'blockquote',
      'hr',
      'br',
      'map',
      'span',
      'div',
      'ul',
      'ol',
      'li',
      'dd',
      'dt',
      'dl',
      'fieldset',
      'legend'
    ],
    allowedAttributes: {
      '*': [
        'id',
        'style',
        'class',
        'title'
      ],
      'thead': [
        'align',
        'charoff',
        'valign',
        'char'
      ],
      'tbody': [
        'align',
        'charoff',
        'valign',
        'char'
      ],
      'tfoot': [
        'align',
        'charoff',
        'valign',
        'char'
      ],
      'font': [
        'face',
        'color',
        'size'
      ],
      'colgroup': [
        'span',
        'width',
        'align',
        'charoff',
        'valign',
        'char'
      ],
      'col': [
        'span',
        'width',
        'align',
        'charoff',
        'valign',
        'char'
      ],
      'tr': [
        'char',
        'height',
        'charoff',
        'width',
        'valign',
        'background'
      ],
      'td': [
        'background',
        'char',
        'charoff',
        'colspan',
        'rowspan',
        'align',
        'valign',
        'abbr',
        'axis',
        'headers',
        'scope',
        'nowrap',
        'height',
        'width',
        'bgcolor'
      ],
      'th': [
        'background',
        'char',
        'charoff',
        'colspan',
        'rowspan',
        'align',
        'valign',
        'abbr',
        'axis',
        'headers',
        'scope',
        'nowrap',
        'height',
        'width',
        'bgcolor'
      ],
      'table': [
        'height',
        'width',
        'cellpadding',
        'cellspacing',
        'bgcolor',
        'background',
        'align',
        'noresize',
        'border'
      ],
      'a': [
        'name',
        'href',
        'nohref'
      ],
      'p': [
        'align'
      ],
      'label': [
        'for'
      ],
      'img': [
        'src',
        'height',
        'width',
        'name',
        'alt',
        'border',
        'hspace',
        'vspace',
        'align'
      ]
    }
  };
  const options = params || policy;
  return sanitize(html, options);
}
