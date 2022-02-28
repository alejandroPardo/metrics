import React, { useState, useEffect } from 'react';
import Chart from '../components/Chart/Chart.jsx';
import { Table, Form, Container } from 'react-bootstrap';

const TransactionsView = () => {
    const[series, setSeries] = useState([]);
    const[counts, setCounts] = useState([]);
    const[tableData, setTableData] = useState([]);
    const[selectedTimeline, setSelectedTimeline] = useState("WEEK");
    const chartsUrl = `http://localhost:8080/v1/metrics/api/summarize?_timeline=${selectedTimeline}&_transactions=true`;
    const tableUrl = `http://localhost:8080/v1/metrics/api/summarize?_timeline=${selectedTimeline}`;

    useEffect(() => {
        fetch(chartsUrl)
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
          });

        fetch(tableUrl)
          .then(response => response.json())
          .then(data => setTableData(data.data));
      },[chartsUrl, selectedTimeline, tableUrl]);

      const renderTableData = () => {
        return tableData.map((key, index) => {
           return (
            <tr key={index}>
              <td>{key.name}</td>
              <td>{key.averagetime.toFixed(2)}</td>
              <td>{key.count}</td>
            </tr>
          )
        })
     }

     const onChange = (e) => {
      setSelectedTimeline(e.target.value);
    }

    return (
      <Container className="pt-4">
          <Form.Select aria-label="Timeline Selector" onChange={onChange}>
            <option value="WEEK">Last Week</option>
            <option value="DAY">Yesterday</option>
            <option value="HOUR">Past hour</option>
            <option value="MINUTE">Past minute</option>
          </Form.Select>

          <Chart title={"Summarized Metrics"} data={series} height={350} yTitle={"Average Time"} unit={"ms."}/>
          <Chart title={""} data={counts} height={150} yTitle={"Calls"} unit={' '}/>
          <Table responsive striped bordered hover data-url={tableData} data-toggle="table">
            <thead>
              <tr>
                <th data-field="name" className="operation-row text-start">Operation</th>
                <th data-field="averageTime" className="text-start">Average Duration (ms.)</th>
                <th data-field="count" className="text-start">Count</th>
              </tr>
            </thead>
            <tbody>
              {renderTableData()}
            </tbody>
          </Table>
        </Container>
    );
}

export default TransactionsView;