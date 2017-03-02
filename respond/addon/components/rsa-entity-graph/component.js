import ForceLayoutComponent from 'respond/components/rsa-force-layout/component';
import ForceLayoutTemplate from 'respond/components/rsa-force-layout/template';
import connect from 'ember-redux/components/connect';
import { storyNodesAndLinks } from 'respond/selectors/storyline';

const DEFAULT_NODE_RADIUS = 25;

const stateToComputed = ({ respond }) => {
  return {
    data: storyNodesAndLinks({ respond, defaultNodeRadius: DEFAULT_NODE_RADIUS })
  };
};

const EntityGraph = ForceLayoutComponent.extend({
  classNames: ['rsa-entity-graph'],
  layout: ForceLayoutTemplate,
  alpha: 0.075,
  nodeRadius: DEFAULT_NODE_RADIUS
});

export default connect(stateToComputed)(EntityGraph);
