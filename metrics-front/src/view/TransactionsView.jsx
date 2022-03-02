import React, { useState, useEffect } from 'react';
import StackChart from '../components/Chart/StackChart.jsx';
import { Table, Form, Container } from 'react-bootstrap';

const TransactionsView = () => {
    const[series, setSeries] = useState([]);
    const[categories, setCategories] = useState([]);
    const[tableData, setTableData] = useState([]);
    const[selectedTimeline, setSelectedTimeline] = useState("WEEK");
    const url = `http://localhost:8080/v1/metrics/api?_timeline=${selectedTimeline}`;

    useEffect(() => {
      fetch(`${url}&_transaction=TRANSACTIONS_AVERAGE`)
        .then(response => response.json())
        .then(data => {
          let seriesData = [];
          let categoriesData = [];
          data.data.keys.forEach(key => {
            seriesData.push({'name': key, 'data':[]});
          });

          data.data.values.forEach(element => {
            categoriesData.push(element.timestamp);
            seriesData.forEach(serie => {
              console.log(serie.name);
              console.log(element.values.hasOwnProperty(serie.name));
              serie.data.push(element.values.hasOwnProperty(serie.name) ? element.values[serie.name] : 0);
            })            
          });
          setSeries(seriesData);
          setCategories(categoriesData);
          console.log(categoriesData);
          console.log(seriesData);
      });

      /*fetch(`${url}&_transaction=TRANSACTIONS`)
        .then(response => response.json())
        .then(data => setTableData(data.data));*/
      },[selectedTimeline]);

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
          <StackChart data={series} categories={categories}/>
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