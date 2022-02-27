import React, { useState, useEffect } from 'react';
import ReactApexChart from 'react-apexcharts';

const url = "http://localhost:8080/v1/metrics/api/summarize?_timeline=WEEK&_transactions=true";

const Chart = (props) => {
  const[series, setSeries] = useState([]);
  
  useEffect(() => {
    fetch(url)
    .then(response => response.json())
    .then(data => {
      let seriesData = [];
      data.data.forEach(element => {
        seriesData.push([Date.parse(element.hour),element.average]);
      });
      setSeries([{"name":"Average","data":seriesData}]);
    })
  },[])

  const [options, setOptions] = useState({
      chart: {
        type: 'area',
        stacked: false,
        height: 350,
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
          text: 'Average Time'
        },
      },
      xaxis: {
        type: 'datetime',
      },
      tooltip: {
        shared: false,
        y: {
          formatter: function (val) {
            return (val).toFixed(0) + "ms."
          }
        }
      }
    });

  return (
    <div>
      <ReactApexChart options={options} series={series} type="area" height={350} />
    </div>
  );
}

export default Chart;