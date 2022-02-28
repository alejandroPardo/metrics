import React, { useState, useEffect } from 'react';
import ReactApexChart from 'react-apexcharts';

const Chart = (props) => {
  const [series, setSeries] = useState(props.data);
  const options = {
    chart: {
      type: 'area',
      stacked: false,
      height: props.height,
      zoom: {
        type: 'x',
        enabled: false,
        autoScaleYaxis: true
      },
    },
    dataLabels: {
      enabled: false
    },
    markers: {
      size: 0,
    },
    title: {
      text: props.title,
      align: 'left'
    },
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        inverseColors: false,
        opacityFrom: 0.5,
        opacityTo: 0,
        stops: [0, 90, 100]
      },
    },
    yaxis: {
      labels: {
        formatter: function (val) {
          return (val).toFixed(0);
        },
      },
      title: {
        text: props.yTitle
      },
    },
    xaxis: {
      type: 'datetime',
    },
    tooltip: {
      shared: false,
      y: {
        formatter: function (val) {
          return `${(val)} ${props.unit}`
        }
      }
    }
  };

    useEffect(() => {
      setSeries(props.data);
    },[props.data, props.height, props.options, props.title, props.unit, props.yTitle])

  return (
    <div>
      <ReactApexChart options={options} series={series} type="area" height={props.height} />
    </div>
  );
}

export default Chart;