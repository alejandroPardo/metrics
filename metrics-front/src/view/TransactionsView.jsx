import React, { useState, useEffect } from 'react';
import StackChart from '../components/Chart/StackChart.jsx';
import { Form, Container } from 'react-bootstrap';
import InformationTable from '../components/Table/InformationTable.jsx'

const TransactionsView = () => {
    const[series, setSeries] = useState([]);
    const[categories, setCategories] = useState([]);
    const[tableData, setTableData] = useState([]);
    const[tableHeaders, setTableHeaders] = useState([]);
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
              serie.data.push(element.values.hasOwnProperty(serie.name) ? element.values[serie.name] : 0);
            })            
          });
          setSeries(seriesData);
          setCategories(categoriesData);
      });

      fetch(`${url}&_transaction=TRANSACTIONS`)
        .then(response => response.json())
        .then(data => {
          let columnData = [];
          for (let key of Object.keys(data.data[0])) {
            if(key !== 'transactionUuid' && key !== 'metricUuid' && key !== 'transactionLevel' && key !== 'code') columnData.push({dataField: key, text: key, sort: true});
          }
          setTableHeaders(columnData)
          setTableData(data.data)
        });
      },[selectedTimeline]);


     /*useEffect(() => {
      async function fetchData() {
          const res = await fetch("http://api-call/?issuenumber=".concat(issueNumber));
          res.json().then(res => setdiagnosisInfo(res));
      }
      // fetchData();
      if (show){
          fetchData();
      }
    }, [issueNumber, show]);*/

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
          <InformationTable data={tableData} columns={tableHeaders} />
        </Container>
    );
}

export default TransactionsView;