import React, { useState, useEffect } from 'react';
import Chart from '../components/Chart/Chart.jsx';

const url = "http://localhost:8080/v1/metrics/api/summarize?_timeline=WEEK&_transactions=true";

const SummarizeView = () => {
    const[series, setSeries] = useState([]);
    const[counts, setCounts] = useState([]);

    useEffect(() => {
        fetch(url)
        .then(response => response.json())
        .then(data => {
          let seriesData = [];
          let countsData = [];
          data.data.forEach(element => {
            seriesData.push([Date.parse(element.hour),element.average]);
            countsData.push([Date.parse(element.hour),element.count]);
          });
          setSeries([{"name":"Average","data":seriesData}]);
          setCounts([{"name":"Count","data":countsData}]);
        })
      },[])

    return (
        <div>
        <Chart title="Summarized Metrics" data={series} height="350"/>
        <Chart title="" data={counts} height="100"/>
        </div>
    );
}

export default SummarizeView;