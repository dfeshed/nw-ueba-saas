import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAlertsForTimeline, getFilter } from 'investigate-users/reducers/alerts/selectors';
import { select } from 'd3-selection';
import { scaleBand, scaleLinear, scaleOrdinal } from 'd3-scale';
import { max } from 'd3-array';
import { stack } from 'd3-shape';
import { axisBottom } from 'd3-axis';
import { timeFormat } from 'd3-time-format';
import computed from 'ember-computed-decorators';
import { updateFilter } from 'investigate-users/actions/alert-details';

const createLegend = (g, keys, width, z) => {
  const legend = g.append('g')
    .attr('class', 'alertsCountAxis')
    .attr('text-anchor', 'end')
    .selectAll('g')
    .data(keys.slice())
    .enter().append('g')
    .attr('transform', (d, i) => `translate(0,${i * 20})`);

  legend.append('rect')
    .attr('x', width - 19)
    .attr('width', 19)
    .attr('height', 19)
    .attr('fill', z);

  legend.append('text')
    .attr('x', width - 24)
    .attr('y', 9.5)
    .attr('dy', '0.32em')
    .text((d) => d);
};


const createTooltip = (svg) => {
  const tooltip = svg.append('g')
    .attr('class', 'tooltip')
    .style('display', 'none');

  tooltip.append('rect')
    .attr('width', 240)
    .attr('height', 20)
    .attr('fill', 'white')
    .style('opacity', 0.5);

  tooltip.append('text')
    .attr('x', '120')
    .attr('dy', '1.2em')
    .style('text-anchor', 'middle')
    .attr('font-size', '12px')
    .attr('font-weight', 'bold');
  return tooltip;
};
const stateToComputed = (state) => ({
  filter: getFilter(state),
  alerts: getAlertsForTimeline(state)
});

const dispatchToActions = {
  updateFilter
};

const OverviewAlertTimelineComponent = Component.extend({

  _renderAlertsTimeLine(data) {
    const _that = this;
    const svg = select('#alertTimeline');
    const margin = { top: 10, right: 40, bottom: 20, left: 40 };
    const width = +svg.node().getBoundingClientRect().width - margin.left - margin.right;
    const height = +svg.node().getBoundingClientRect().height - margin.top - margin.bottom;
    const g = svg.append('g').attr('transform', `translate(${margin.left},${margin.top})`);
    const x = scaleBand().rangeRound([0, width]).paddingInner(0.05).align(0.1);
    const y = scaleLinear().rangeRound([height, 0]);
    const z = scaleOrdinal().range(['rgb(104, 159, 56)', 'rgb(255, 160, 0)', 'rgb(230, 74, 25)', 'rgb(201, 24, 24)']);
    const keys = ['Low', 'Medium', 'High', 'Critical'];
    x.domain(data.map((d) => d.day));
    y.domain([0, max(data, (d) => d.total)]).nice();
    z.domain(keys);
    data = stack().keys(keys)(data);
    data = data.map((d) => {
      const mappedArray = d.map((x) => ({ ...x, name: d.key }));
      mappedArray.key = d.key;
      return mappedArray;
    });

    g.append('g')
      .selectAll('g')
      .data(data)
      .enter().append('g')
      .attr('fill', (d) => z(d.key))
      .selectAll('rect')
      .data((d) => d)
      .enter().append('rect')
      .attr('x', (d) => x(d.data.day) + x.bandwidth() / 2 - 10)
      .attr('y', (d) => y(d[1]))
      .attr('height', (d) => y(d[0]) - y(d[1]))
      .attr('width', 20)
      .on('mouseover', () => {
        tooltip.style('display', null);
      })
      .on('mouseout', () => {
        tooltip.style('display', 'none');
      })
      .on('mousemove', (d) => {
        const xPosition = event.offsetX - 150;
        const yPosition = event.offsetY - 25;
        tooltip.attr('transform', `translate(${xPosition},${yPosition})`);
        tooltip.select('text').text(`${d[1] - d[0]} ${d.name} alerts occurred on ${timeFormat('%b %d')(d.data.day)}`);
      })
      .on('click', (d) => {
        _that.applyFilter(d.name);
      });

    g.append('g')
      .attr('class', 'alertsDateAxis')
      .attr('transform', `translate(0,${height})`)
      .call(axisBottom(x).tickFormat(timeFormat('%b %d')));

    createLegend(g, keys, width, z);
    const tooltip = createTooltip(svg);

  },
  applyFilter(selection) {
    this.send('updateFilter', null, true);
    this.get('applyAlertsFilter')(this.get('filter').merge({ severity: [selection] }));
  },

  @computed('alerts')
  alertsTimeline(alerts) {
    if (alerts) {
      this._renderAlertsTimeLine(alerts);
      return true;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(OverviewAlertTimelineComponent);