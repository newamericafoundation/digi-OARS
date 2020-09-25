import React, { useEffect, useState } from "react";
import { CCard, CCardHeader, CCardBody } from "@coreui/react";
import { useAuth } from "../auth-hook";
import { Widgets } from "../pages/views/home/Widgets";
import { Jumbotron } from "../pages/views/home/Jumbotron";
import { WelcomeCallout } from "../pages/views/home/WelcomeCallout";
import {
  CChartBar,
  CChartLine,
  CChartDoughnut,
  CChartRadar,
  CChartPie,
  CChartPolarArea
} from '@coreui/react-chartjs'
import axios from "axios";

const HomePage = () => {
  const auth = useAuth();
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const data = axios.get("http://" + window._env_.API_CLIENT_URL + ":10200/api/fund/aggregate?startDate=9-21-2020&endDate=9-25-2020")

    data.then((response) => setChartData(response.data.entity))
  }, [])

  const getFlat = () => {
    let temp = {}
    if (chartData) {
      Object.keys(chartData).forEach(element => {
        // console.log(chartData[element])
        temp[element] = chartData[element]["totalAmount"];
      });
    }
    return temp
  }
  return (
    <>
      <Jumbotron auth={auth} />
      <WelcomeCallout auth={auth} />
      <Widgets auth={auth} />
      <CCard>
        <CCardHeader>
          Bar Chart
          <div className="card-header-actions">
            <a href="http://www.chartjs.org" className="card-header-action">
              <small className="text-muted">docs</small>
            </a>
          </div>
        </CCardHeader>
        <CCardBody>
          <CChartBar
            type="bar"
            datasets={[
              {
                label: 'GitHub Commits',
                backgroundColor: '#f87979',
                data: Object.values(getFlat())
              }
            ]}
            labels={Object.keys(getFlat())}
            options={{
              tooltips: {
                enabled: true
              }
            }}
          />
        </CCardBody>
      </CCard>
    </>
  );
};

export default HomePage;
