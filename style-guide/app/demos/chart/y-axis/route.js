import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  model() {
    return {
      title: 'Y Axis',
      description: 'Creates an SVG G element used to group Text and Path elements that represent the Y axis values.',
      properties: [
        {
          name: 'scale',
          type: 'function',
          required: true,
          description: 'A function that scales the domain values to pixel values.'
        },
        {
          name: 'rotation',
          type: 'number',
          required: false,
          description: 'The degrees to rotate the axis.',
          default: 0
        }
      ],
      singleSeries: [
        [
          { x: 1412011600000, y: 57240797 },
          { x: 1412035200000, y: 40802921 },
          { x: 1412208000000, y: 54824229 },
          { x: 1412294400000, y: 62621099 },
          { x: 1412380800000, y: 60796090 },
          { x: 1412467200000, y: 46186237 },
          { x: 1412553600000, y: 53948266 },
          { x: 1412640000000, y: 63232914 },
          { x: 1412726400000, y: 52186213 },
          { x: 1412812800000, y: 3251360 }
        ]
      ]
    };
  }
});
