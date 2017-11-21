package com.team.atapp.service;

import java.util.List;
import java.util.TreeMap;

import com.team.atapp.domain.TblBookedCarInfo;
import com.team.atapp.domain.TblCarManufacture;
import com.team.atapp.domain.TblCarModel;
import com.team.atapp.domain.TblHelplineContact;
import com.team.atapp.domain.TblServiceProvider;
import com.team.atapp.domain.TblUserCarInfo;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;

/**
 * 
 * @author Vikky
 *
 */
public interface ConsumerInstrumentService {
	
	public UserLoginDTO mobileLoginAuth(String userType, String mobilenumber, String password) throws AtAppException;

	public UserLoginDTO getRefreshTokenOnBaseToken()throws AtAppException;

	public List<TblUserInfo> getUserInfosCount()throws AtAppException;

	public List<TblUserInfo> getUserInfos()throws AtAppException;

	public TblUserInfo getUserById(String userId)throws AtAppException;

	public TblUserInfo updateUser(TblUserInfo userInfo)throws AtAppException;

	public TblUserInfo getUserByEmailId(String emailId, String usrtype)throws AtAppException;

	public TblUserCarInfo getUserCarByRegNo(String regNo)throws AtAppException;

	public TblServiceProvider getSPById(String id)throws AtAppException;

	public void updateUserCarInfo(TblUserCarInfo uCarInfo)throws AtAppException;

	public TblUserCarInfo saveCar(TblUserCarInfo newCar)throws AtAppException;

	public List<TblServiceProvider> getSpDetails() throws AtAppException;

	public double getDistanceToLatLng(double curLat, double curLng, double existLat, double existLng) throws AtAppException;

	public void updateSP(TblServiceProvider spInfo)throws AtAppException;

	public List<TblCarManufacture> getCarManufacturer() throws AtAppException;

	public TblCarModel getCarModelById(String id) throws AtAppException;

	public void saveImageDB(TblCarModel carModel);

	public TblCarModel getCarModelByName(String carName) throws AtAppException;

	public void deleteCarInfoByUser(TblUserCarInfo uCar)throws AtAppException;

	public List<TblHelplineContact> getHelplineNo()throws AtAppException;

	public TblUserInfo setGeneratedPwd(TblUserInfo userInfo)throws AtAppException;
	
	public String getPasswordResetMessage(TblUserInfo userInfo) throws AtAppException;

	public List<TblUserCarInfo> getUsersCarByRegNo(String regNo)throws AtAppException;

	public TblBookedCarInfo updateBookedCarInfo(TblBookedCarInfo bookedCar)throws AtAppException;

	public TblUserInfo getUserEmailId(String emailId)throws AtAppException;

	public TblServiceProvider getSpByEmailId(String emailId)throws AtAppException;

	public TblServiceProvider setGeneratedPwdForSP(TblServiceProvider spInfo)throws AtAppException;

	public String getPasswordResetMessageForSp(TblServiceProvider sp)throws AtAppException;

	public List<TblBookedCarInfo> getBookedCarByspId(String id, String slot)throws AtAppException;

	public String getCarBookedServiceMessage(TblUserInfo userInfo, TblBookedCarInfo bookedCarUpdated)throws AtAppException;

	public String getCarCancellationServiceMessage(TblUserInfo userInfo, TblBookedCarInfo bCarInfo)throws AtAppException;

	public String getCarCompletionServiceMessage(TblUserInfo tblUserInfo, TblBookedCarInfo bookedCar)throws AtAppException;

	public String getReferMessage(TblUserInfo userInfo, String referralCode)throws AtAppException;

	public String getSpReferMessage(TblServiceProvider spInfo, String referralCode)throws AtAppException;

	
	
}
