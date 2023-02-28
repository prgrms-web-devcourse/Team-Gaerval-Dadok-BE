package com.dadok.gaerval.domain.job.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum JobGroup implements EnumType {

	DEVELOPMENT("개발",
		Arrays.asList(
			JobName.DATA_ENGINEER,
			JobName.JAVA_DEVELOPER,
			JobName.DOTNET_DEVELOPER,
			JobName.SYSTEM_NETWORK_ADMINISTRATOR,
			JobName.BACKEND_DEVELOPER,
			JobName.FRONTEND_DEVELOPER,
			JobName.SECURITY_ENGINEER,
			JobName.HARDWARE_ENGINEER,
			JobName.DEVOPS,
			JobName.QA_TEST_ENGINEER,
			JobName.ANDROID_DEVELOPER,
			JobName.IOS_DEVELOPER,
			JobName.CIO,
			JobName.CTO,
			JobName.SERVER_DEVELOPER,
			JobName.WEB_DEVELOPER,
			JobName.PRODUCT_MANAGER,
			JobName.DEVELOPMENT_MANAGER,
			JobName.PHP_DEVELOPER,
			JobName.RUBY_ON_RAILS_DEVELOPER,
			JobName.NODEJS_DEVELOPER,
			JobName.MEDIA_ENGINEER,
			JobName.GRAPHICS_ENGINEER,
			JobName.PYTHON_DEVELOPER,
			JobName.C_CPP_DEVELOPER,
			JobName.WEB_PUBLISHER,
			JobName.BI_ENGINEER,
			JobName.DATA_SCIENTIST,
			JobName.BIGDATA_ENGINEER,
			JobName.TECHNICAL_SUPPORT,
			JobName.BLOCKCHAIN_ENGINEER,
			JobName.MACHINE_LEARNING_ENGINEER,
			JobName.SOFTWARE_ENGINEER,
			JobName.CROSS_PLATFORM_APP_DEVELOPER,
			JobName.VR_ENGINEER,
			JobName.ERP_EXPERT,
			JobName.ETC)),

	MANAGEMENT_BUSINESS("경영/비즈니스",
		Arrays.asList(JobName.SECRETARY,
			JobName.OPERATION_MANAGER,
			JobName.OFFICE_MANAGER,
			JobName.PM_PO,
			JobName.BUSINESS_PLANNER,
			JobName.SERVICE_PLANNER,
			JobName.CONSULTANT,
			JobName.CEO,
			JobName.CFO,
			JobName.COO,
			JobName.CSO,
			JobName.ACCOUNTANT,
			JobName.OVERSEAS_BUSINESS_PLANNER,
			JobName.EXHIBITION_PLANNER,
			JobName.SEMINAR_FORUM_PLANNER,
			JobName.PERFORMANCE_PLANNER,
			JobName.AGILE_COACH,
			JobName.ADMINISTRATIVE_ASSISTANT,
			JobName.PRODUCT_PLANNER_BM,
			JobName.ISMS,
			JobName.ETC)),

	MARKETING_ADVERTISEMENT("마케팅/광고",
		Arrays.asList(JobName.BRAND_MARKETER,
			JobName.COPYWRITER,
			JobName.MARKETER,
			JobName.PR_EXPERT,
			JobName.MARKETING_PLANNER,
			JobName.SOCIAL_MEDIA_MARKETER,
			JobName.ADVERTISING_PLANNER_AE,
			JobName.CMO,
			JobName.CBO,
			JobName.GLOBAL_MARKETING,
			JobName.ETC)),

	SALES("영업",
		Arrays.asList(JobName.SALES,
			JobName.KEY_ACCOUNT_MANAGER,
			JobName.SALES_ENGINEER,
			JobName.SALES_MANAGER,
			JobName.SOLUTION_CONSULTANT,
			JobName.CUSTOMER_SUCCESS_MANAGER,
			JobName.ETC)),

	CUSTOMER_SERVICE_RETAIL("고객서비스 / 리테일",
		Arrays.asList(JobName.CS_ADVISOR,
			JobName.FLIGHT_ATTENDANT,
			JobName.AS_TECHNICIAN,
			JobName.EVENT_PLANNER,
			JobName.FLORIST,
			JobName.TRAVEL_AGENT,
			JobName.STORE_MANAGER,
			JobName.STORE_STAFF,
			JobName.SKIN_CARE_SPECIALIST,
			JobName.HAIR_DESIGNER,
			JobName.VISUAL_MECHANDISER,
			JobName.MD,
			JobName.INBOUND_TELEMARKETER,
			JobName.CRM_EXPERT,
			JobName.OUTBOUND_TELEMARKETER,
			JobName.CS_MANAGER,
			JobName.HEALTHCARE_MANAGER,
			JobName.BEAUTICIAN,
			JobName.PET_BEAUTICIAN,
			JobName.RECEPTIONIST,
			JobName.ETC)),

	MEDIA("미디어",
		Arrays.asList(
			JobName.VIDEO_EDITOR,
			JobName.WRITER,
			JobName.EDITOR,
			JobName.JOURNALIST,
			JobName.PD,
			JobName.REPORTER,
			JobName.PHOTOGRAPHER,
			JobName.TRANSLATOR_INTERPRETER,
			JobName.PUBLISHING_PLANNER,
			JobName.AUDIO_ENGINEER,
			JobName.CURATOR,
			JobName.CONTENT_CREATOR,
			JobName.ENTERTAINMENT,
			JobName.ETC
		)
	),

	GAME("게임 제작",
		Arrays.asList(JobName.UNITY_DEVELOPER,
			JobName.GAME_GRAPHIC_DESIGNER,
			JobName.GAME_ARTIST,
			JobName.GAME_PLANNER,
			JobName.UNREAL_DEVELOPER,
			JobName.GAME_OPERATOR_GM,
			JobName.GAME_SERVER_DEVELOPER,
			JobName.GAME_CLIENT_DEVELOPER,
			JobName.MOBILE_GAME_DEVELOPER,
			JobName.ETC)),

	HR("HR",

		Arrays.asList(JobName.TEACHER,
			JobName.TECHNICAL_EDUCATION,
			JobName.HR_CONSULTANT,
			JobName.PERSONNEL_MANAGER,
			JobName.RECRUITER,
			JobName.HRD,
			JobName.ORGANIZATIONAL_CULTURE,
			JobName.SALARY_ADMINISTRATOR,
			JobName.E_LEARNING,
			JobName.HEADHUNTER,
			JobName.PROFESSOR,
			JobName.HRBP,
			JobName.ETC)),

	ENGINEERING_DESIGN("엔지니어링 / 설계",
		Arrays.asList(JobName.AEROSPACE_ENGINEER,
			JobName.AGRICULTURAL_ENGINEER,
			JobName.AUTOMOTIVE_ENGINEER,
			JobName.GENETIC_ENGINEER,
			JobName.BIOMEDICAL_ENGINEER,
			JobName.CERAMIC_ENGINEER,
			JobName.CHEMICAL_ENGINEER,
			JobName.CIVIL_ENGINEERING_TECHNICIAN,
			JobName.COMMISSIONING_ENGINEER,
			JobName.CONSTRUCTION_ENGINEER,
			JobName.CONTROL_ENGINEER,
			JobName.DRAFTING_TECHNICIAN,
			JobName.DRILLING_ENGINEER,
			JobName.ELECTRICAL_ENGINEER,
			JobName.ELECTROMECHANICAL_ENGINEER,
			JobName.ELECTRONICS_ENGINEER,
			JobName.ENVIRONMENTAL_ENGINEER,
			JobName.EQUIPMENT_ENGINEER,
			JobName.FACILITY_ENGINEER,
			JobName.FIRE_SAFETY_TECHNICIAN,
			JobName.GEOGRAPHER,
			JobName.GEOLOGICAL_ENGINEER,
			JobName.INDUSTRIAL_ENGINEER,
			JobName.INSTRUMENTATION_AND_CONTROL_ENGINEER,
			JobName.MANUFACTURING_ENGINEER,
			JobName.MARINE_ENGINEER,
			JobName.MATERIALS_ENGINEER,
			JobName.MECHANICAL_ENGINEER,
			JobName.METALLURGICAL_ENGINEER,
			JobName.MINING_TECHNICIAN,
			JobName.NAVAL_ARCHITECT,
			JobName.NUCLEAR_ENERGY_ENGINEER,
			JobName.OIL_ENGINEER,
			JobName.PIPELINE_DESIGN_ENGINEER,
			JobName.PLANT_ENGINEER,
			JobName.PROCESS_ENGINEER,
			JobName.PRODUCT_ENGINEER,
			JobName.PROJECT_ENGINEER,
			JobName.QA_ENGINEER,
			JobName.QC_ENGINEER,
			JobName.ELECTRICAL_POWER_ENGINEER,
			JobName.RESERVOIR_ENGINEER,
			JobName.RF_ENGINEER,
			JobName.ROTATING_EQUIPMENT_ENGINEER,
			JobName.STRUCTURAL_ENGINEER,
			JobName.TURBINE_ENGINEER,
			JobName.CAD_3D_DESIGNER,
			JobName.ROBOT_AUTOMATION,
			JobName.ETC)),

	FINANCE("금융",
		Arrays.asList(
			JobName.ACCOUNTANT,
			JobName.FINANCIAL_ANALYST,
			JobName.CERTIFIED_PUBLIC_ACCOUNTANT,
			JobName.INTERNAL_CONTROL_SPECIALIST,
			JobName.INSURANCE_AGENT,
			JobName.ADJUSTER,
			JobName.CLAIMS_ADJUSTER,
			JobName.UNDERWRITER,
			JobName.REAL_ESTATE_ASSET_MANAGER,
			JobName.REAL_ESTATE_AGENT,
			JobName.APPRAISER,
			JobName.ANALYST,
			JobName.INVESTOR_RELATIONS,
			JobName.TRADER,
			JobName.FINANCIAL_ENGINEER,
			JobName.ASSET_MANAGER,
			JobName.ASSET_MANAGER,
			JobName.INVESTMENT_BANKER,
			JobName.COMPLIANCE_OFFICER,
			JobName.TAX_ACCOUNTANT,
			JobName.FINANCIAL_MANAGER,
			JobName.INVESTMENT_SECURITIES,
			JobName.ETC
		)),

	MANUFACTURING("제조 / 생산",
		Arrays.asList(
			JobName.ASSEMBLY_TECHNICIAN,
			JobName.CHEMIST,
			JobName.MECHANICAL_TECHNICIAN,
			JobName.MANUFACTURING_ENGINEER,
			JobName.MECHANICAL_FACILITY_DESIGN,
			JobName.PRODUCTION_MANAGER,
			JobName.PRODUCTION_WORKER,
			JobName.PROCESS_MANAGER,
			JobName.QUALITY_MANAGER,
			JobName.SAFETY_MANAGER,
			JobName.TEST_ENGINEER,
			JobName.TEXTILES_APPAREL_FASHION,
			JobName.SEMICONDUCTOR_DISPLAY,
			JobName.ETC
		)),

	MEDICAL("의료 / 제약 / 바이오",
		Arrays.asList(
			JobName.DENTAL_HYGIENIST,
			JobName.PARALEGAL,
			JobName.CLINICAL_LABORATORY_TECHNOLOGIST,
			JobName.NURSE,
			JobName.PHYSICAL_THERAPIST,
			JobName.PHYSICIAN,
			JobName.VETERINARIAN,
			JobName.NURSE_PARALEGAL,
			JobName.DENTIST,
			JobName.EXAMINER,
			JobName.PHARMACIST,
			JobName.CLINICAL_TRIAL_RESEARCHER,
			JobName.CLINICAL_TRIAL_NURSE,
			JobName.MICROBIOLOGIST,
			JobName.GENETICIST,
			JobName.RESEARCHER,
			JobName.ORIENTAL_MEDICINE_PHYSICIAN,
			JobName.RADIOLOGIST,
			JobName.HOSPITAL_COORDINATOR,
			JobName.CLINICAL_PSYCHOLOGIST,
			JobName.ETC
		)
	),

	LOGISTICS("물류 / 무역",
		Arrays.asList(
			JobName.SHIPPING_HANDLER,
			JobName.DIESEL_MECHANIC,
			JobName.OPERATIONS_MANAGER,
			JobName.FORKLIFT_DRIVER,
			JobName.DRIVER,
			JobName.LOGISTICS_ANALYST,
			JobName.FREIGHT_TRUCK_DRIVER,
			JobName.SHIPPING_CLERK,
			JobName.TRANSPORTATION_MANAGER,
			JobName.WAREHOUSE_WORKER,
			JobName.AIR_TRANSPORTATION,
			JobName.MARINE_TRANSPORTATION,
			JobName.TRADE_OFFICE,
			JobName.CUSTOMS_BROKER,
			JobName.ORIGIN_MANAGEMENT_SPECIALIST,
			JobName.INBOUND_OUTBOUND_MANAGER,
			JobName.DISTRIBUTION_MANAGER,
			JobName.MARINE_NAVIGATION_OFFICER,
			JobName.MECHANICAL_OFFICER,
			JobName.ETC
		)
	),

	EDUCATION("교육",
		Arrays.asList(
			JobName.CURRICULUM_PLANNER,
			JobName.PRIVATE_TUTOR,
			JobName.SCHOOL_STAFF,
			JobName.KINDERGARTEN_TEACHER,
			JobName.TEACHER,
			JobName.QUALIFICATION_TECHNICAL_INSTRUCTOR,
			JobName.ETC
		)
	),
	FOOD_BEVERAGE("식/음료",
		Arrays.asList(
			JobName.NUTRITIONIST,
			JobName.CHEF_BAKER,
			JobName.BARTENDER,
			JobName.COOK,
			JobName.FOOD_SERVICE_WORKER,
			JobName.RESTAURANT_MANAGER,
			JobName.FOOD_MD,
			JobName.FOOD_STYLIST,
			JobName.SOMMELIER,
			JobName.FOOD_PROCESSING_DEVELOPMENT,
			JobName.BARISTA,
			JobName.ETC
		)
	),

	CONSTRUCTION_FACILITY("건설 / 시설",
		Arrays.asList(
			JobName.CARPENTER,
			JobName.HEAVY_EQUIPMENT_TECHNICIAN,
			JobName.MAINTENANCE_WORKER,
			JobName.MAINTENANCE_MANAGER,
			JobName.FACILITY_MANAGER,
			JobName.ELECTRICIAN,
			JobName.PLANT_MANAGER,
			JobName.WELDER,
			JobName.PLUMBER,
			JobName.ESTIMATOR,
			JobName.ARCHITECT,
			JobName.INTERIOR_ARCHITECT,
			JobName.CONSTRUCTION_SUPERVISOR,
			JobName.ETC
		)
	),

	PUBLIC_WELFARE("공공 / 복지",
		Arrays.asList(
			JobName.COUNSELOR,
			JobName.FIREFIGHTER,
			JobName.ENVIRONMENTAL_EXPERT,
			JobName.INFORMATION_ANALYST,
			JobName.LIFEGUARD,
			JobName.REGIONAL_EXPERT,
			JobName.PUBLIC_OFFICIAL,
			JobName.PROFESSIONAL_SOLDIER,
			JobName.VOLUNTEER,
			JobName.SOCIAL_WORKER,
			JobName.PARAMEDIC,
			JobName.CAREGIVER,
			JobName.ETC
		)
	);

	private final String groupName;
	private final List<JobName> jobNames;

	JobGroup(String groupName, List<JobName> jobNames) {
		this.groupName = groupName;
		this.jobNames = jobNames;
	}

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDescription() {
		return this.groupName;
	}

	public static JobName findSubJob(String jobName) {
		return Arrays.stream(values())
			.flatMap(jobGroup -> jobGroup.getJobNames().stream())
			.filter(j -> Objects.equals(j.name(), jobName.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(jobName, "jobName"));
	}

	public static JobGroup findJobGroup(String groupName) {
		return Arrays.stream(values())
			.filter(j -> Objects.equals(j.getDescription(), groupName.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(groupName, "groupName"));
	}

	@JsonCreator
	public static JobGroup create(String groupName) {
		return Arrays.stream(values())
			.filter(jobGroup -> Objects.equals(jobGroup.name(), groupName.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(groupName, "jobGroup"));
	}

	@Getter
	public enum JobName implements EnumType {
		// 개발
		SECURITY_ENGINEER("보안 엔지니어"),
		HARDWARE_ENGINEER("하드웨어 엔지니어"),
		DATA_ENGINEER("데이터 엔지니어"),
		JAVA_DEVELOPER("자바 개발자"),
		DOTNET_DEVELOPER(".NET 개발자"),
		SYSTEM_NETWORK_ADMINISTRATOR("시스템,네트워크 관리자"),
		BACKEND_DEVELOPER("백엔드 개발자"),
		FRONTEND_DEVELOPER("프론트엔드 개발자"),
		DEVOPS("DevOps / 시스템 관리자"),
		QA_TEST_ENGINEER("QA,테스트 엔지니어"),
		ANDROID_DEVELOPER("안드로이드 개발자"),
		IOS_DEVELOPER("iOS 개발자"),
		CIO("CIO,Chief Information Officer"),
		CTO("CTO,Chief Technology Officer"),
		SERVER_DEVELOPER("서버 개발자"),
		WEB_DEVELOPER("웹 개발자"),
		PRODUCT_MANAGER("프로덕트 매니저"),
		DEVELOPMENT_MANAGER("개발 매니저"),
		PHP_DEVELOPER("PHP 개발자"),
		RUBY_ON_RAILS_DEVELOPER("루비온레일즈 개발자"),
		NODEJS_DEVELOPER("Node.js 개발자"),
		MEDIA_ENGINEER("영상,음성 엔지니어"),
		GRAPHICS_ENGINEER("그래픽스 엔지니어"),
		PYTHON_DEVELOPER("파이썬 개발자"),
		C_CPP_DEVELOPER("C,C++ 개발자"),
		WEB_PUBLISHER("웹 퍼블리셔"),
		BI_ENGINEER("BI 엔지니어"),
		DATA_SCIENTIST("데이터 사이언티스트"),
		BIGDATA_ENGINEER("빅데이터 엔지니어"),
		TECHNICAL_SUPPORT("기술지원"),
		BLOCKCHAIN_ENGINEER("블록체인 플랫폼 엔지니어"),
		MACHINE_LEARNING_ENGINEER("머신러닝 엔지니어"),
		SOFTWARE_ENGINEER("소프트웨어 엔지니어"),
		CROSS_PLATFORM_APP_DEVELOPER("크로스플랫폼 앱 개발자"),
		VR_ENGINEER("VR 엔지니어"),
		ERP_EXPERT("ERP전문가"),

		// 경영, 비즈니스
		SECRETARY("비서"),
		OPERATION_MANAGER("운영 매니저"),
		OFFICE_MANAGER("오피스 관리 / 매니저"),
		PM_PO("PM/PO"),
		BUSINESS_PLANNER("사업개발/기획자"),
		SERVICE_PLANNER("서비스 기획자"),
		CONSULTANT("컨설턴트"),
		CEO("CEO,Chief Executive Officer"),
		CFO("CFO,Chief Financial Officer"),
		COO("COO,Chief Operation Officer"),
		CSO("CSO,Chief Strategy Officer"),
		ACCOUNTANT("회계/경리"),
		OVERSEAS_BUSINESS_PLANNER("해외 사업개발/기획자"),
		EXHIBITION_PLANNER("전시 기획자"),
		DISPLAY_PLANNER("전시 기획자"),
		BUSINESS_DEVELOPER_OVERSEAS("해외 사업개발/기획자"),
		SEMINAR_FORUM_PLANNER("세미나/포럼 기획자"),
		PERFORMANCE_PLANNER("공연 기획자"),
		AGILE_COACH("애자일코치"),
		ADMINISTRATIVE_ASSISTANT("사무보조"),
		PRODUCT_PLANNER_BM("상품기획자(BM)"),
		ISMS("ISMS(정보보호체계)"),

		// 마케팅/광고
		BRAND_MARKETER("브랜드 마케터"),
		COPYWRITER("카피라이터"),
		MARKETER("마케터"),
		PR_EXPERT("PR 전문가"),
		MARKETING_PLANNER("마케팅 전략 기획자"),
		SOCIAL_MEDIA_MARKETER("소셜 마케터"),
		ADVERTISING_PLANNER_AE("광고 기획자(AE)"),
		CMO("CMO,Chief Marketing Officer"),
		CBO("CBO,Chief Brand Officer"),
		GLOBAL_MARKETING("글로벌 마케팅"),
		OTHER_MARKETING("기타"),

		// 디자인
		GRAPHIC_DESIGNER("그래픽 디자이너"),
		ART_DIRECTOR("아트 디렉터"),
		WEB_DESIGNER("웹 디자이너"),
		MOBILE_DESIGNER("모바일 디자이너"),
		ILLUSTRATOR("일러스트레이터"),
		UI_UX_DESIGNER("UI, UX 디자이너"),
		INTERIOR_DESIGNER("인테리어 디자이너"),
		VIDEO_MOTION_DESIGNER("영상,모션 디자이너"),
		PRODUCT_DESIGNER("제품 디자이너"),
		LANDSCAPE_DESIGNER("조경 디자이너"),
		SPACE_DESIGNER("공간 디자이너"),
		BI_BX_DESIGNER("BI/BX 디자이너"),
		THREE_D_DESIGNER("3D 디자이너"),
		CHARACTER_DESIGNER("캐릭터 디자이너"),
		PUBLISHING_EDITOR("출판, 편집 디자이너"),
		FASHION_DESIGNER("패션 디자이너"),
		PACKAGE_DESIGNER("패키지 디자이너"),

		// 영업
		SALES("영업"),
		KEY_ACCOUNT_MANAGER("주요고객사 담당자"),
		SALES_ENGINEER("세일즈 엔지니어"),
		SALES_MANAGER("영업 관리자"),
		SOLUTION_CONSULTANT("솔루션 컨설턴트"),
		CUSTOMER_SUCCESS_MANAGER("고객성공매니저"),
		OTHER_SALES("기타"),

		// 고객서비스 / 리테일
		CS_ADVISOR("CS 어드바이저"),
		FLIGHT_ATTENDANT("승무원"),
		AS_TECHNICIAN("AS 기술자"),
		EVENT_PLANNER("이벤트 기획자"),
		FLORIST("플로리스트"),
		TRAVEL_AGENT("여행 에이전트"),
		STORE_MANAGER("매장 관리자"),
		STORE_STAFF("매장점원"),
		SKIN_CARE_SPECIALIST("피부관리사"),
		HAIR_DESIGNER("헤어 디자이너"),
		VISUAL_MECHANDISER("비주얼머천다이저"),
		MD("MD"),
		INBOUND_TELEMARKETER("인바운드 텔레마케터"),
		CRM_EXPERT("CRM 전문가"),
		OUTBOUND_TELEMARKETER("아웃바운드 텔레마케터"),
		CS_MANAGER("CS 매니저"),
		HEALTHCARE_MANAGER("헬스케어매니저"),
		BEAUTICIAN("미용사"),
		PET_BEAUTICIAN("애견 미용사"),
		RECEPTIONIST("리셉션"),
		OTHER_CUSTOMER_SERVICE_RETAIL("기타"),

		// 미디어
		VIDEO_EDITOR("영상 편집가"),
		WRITER("작가"),
		EDITOR("에디터"),
		JOURNALIST("저널리스트"),
		PD("PD"),
		REPORTER("리포터"),
		PHOTOGRAPHER("사진작가"),
		TRANSLATOR_INTERPRETER("통/번역"),
		PUBLISHING_PLANNER("출판 기획자"),
		AUDIO_ENGINEER("음향 엔지니어"),
		CURATOR("큐레이터"),
		CONTENT_CREATOR("콘텐츠 크리에이터"),
		ENTERTAINMENT("연예, 엔터테인먼트"),

		// 게임 개발
		UNITY_DEVELOPER("유니티 개발자"),
		GAME_GRAPHIC_DESIGNER("게임 그래픽 디자이너"),
		GAME_ARTIST("게임 아티스트"),
		GAME_PLANNER("게임 기획자"),
		UNREAL_DEVELOPER("언리얼 개발자"),
		GAME_OPERATOR_GM("게임운영자(GM)"),
		GAME_SERVER_DEVELOPER("게임 서버 개발자"),
		GAME_CLIENT_DEVELOPER("게임 클라이언트 개발자"),
		MOBILE_GAME_DEVELOPER("모바일 게임 개발자"),

		// HR
		TECHNICAL_EDUCATION("기술 교육"),
		HR_CONSULTANT("HR 컨설턴트"),
		PERSONNEL_MANAGER("인사담당"),
		RECRUITER("리크루터"),
		HRD("HRD"),
		ORGANIZATIONAL_CULTURE("조직문화"),
		SALARY_ADMINISTRATOR("급여담당"),
		E_LEARNING("E-러닝"),
		HEADHUNTER("헤드헌터"),
		PROFESSOR("교수"),
		HRBP("HRBP"),

		// 엔지니어링 / 설계
		AEROSPACE_ENGINEER("항공우주 공학자"),
		AGRICULTURAL_ENGINEER("농업 공학자"),
		AUTOMOTIVE_ENGINEER("자동차 공학자"),
		GENETIC_ENGINEER("유전 공학자"),
		BIOMEDICAL_ENGINEER("생물의학자"),
		CERAMIC_ENGINEER("세라믹 엔지니어"),
		CHEMICAL_ENGINEER("화학공학 엔지니어"),
		CIVIL_ENGINEERING_TECHNICIAN("토목기사"),
		COMMISSIONING_ENGINEER("시운전 엔지니어"),
		CONSTRUCTION_ENGINEER("건설 엔지니어"),
		CONTROL_ENGINEER("제어 엔지니어"),
		DRAFTING_TECHNICIAN("도면 작성가"),
		DRILLING_ENGINEER("시추 엔지니어"),
		ELECTRICAL_ENGINEER("전기 엔지니어"),
		ELECTROMECHANICAL_ENGINEER("전기기계 공학자"),
		ELECTRONICS_ENGINEER("전자 엔지니어"),
		ENVIRONMENTAL_ENGINEER("환경 엔지니어"),
		EQUIPMENT_ENGINEER("장비 엔지니어"),
		FACILITY_ENGINEER("설비 엔지니어"),
		FIRE_SAFETY_TECHNICIAN("소방안전 기술자"),
		GEOGRAPHER("지리학자"),
		GEOLOGICAL_ENGINEER("지질공학자"),
		INDUSTRIAL_ENGINEER("산업 엔지니어"),
		INSTRUMENTATION_AND_CONTROL_ENGINEER("I&C 엔지니어"),
		MANUFACTURING_ENGINEER("생산공학 엔지니어"),
		MARINE_ENGINEER("해양공학자"),
		MATERIALS_ENGINEER("재료공학자"),
		MECHANICAL_ENGINEER("기계 엔지니어"),
		METALLURGICAL_ENGINEER("금속 공학자"),
		MINING_TECHNICIAN("광산 기술자"),
		NAVAL_ARCHITECT("선박 공학자"),
		NUCLEAR_ENERGY_ENGINEER("원자력/에너지"),
		OIL_ENGINEER("석유공학 엔지니어"),
		PIPELINE_DESIGN_ENGINEER("배관설계 엔지니어"),
		PLANT_ENGINEER("플랜트 엔지니어"),
		PROCESS_ENGINEER("공정 엔지니어"),
		PRODUCT_ENGINEER("제품 엔지니어"),
		PROJECT_ENGINEER("프로젝트 엔지니어"),
		QA_ENGINEER("QA 엔지니어"),
		QC_ENGINEER("QC 엔지니어"),
		ELECTRICAL_POWER_ENGINEER("전자계전 엔지니어"),
		RESERVOIR_ENGINEER("저수처리 엔지니어"),
		RF_ENGINEER("RF 엔지니어"),
		ROTATING_EQUIPMENT_ENGINEER("회전기계 엔지니어"),
		STRUCTURAL_ENGINEER("구조공학 엔지니어"),
		TURBINE_ENGINEER("터빈공학자"),
		CAD_3D_DESIGNER("CAD/3D 설계자"),
		ROBOT_AUTOMATION("로봇/자동화"),

		// 금융
		FINANCIAL_ANALYST("재무 분석가"),
		CERTIFIED_PUBLIC_ACCOUNTANT("공인회계사"),
		INTERNAL_CONTROL_SPECIALIST("내부통제 담당자"),
		INSURANCE_AGENT("보험 에이전트"),
		ADJUSTER("손해 사정관"),
		CLAIMS_ADJUSTER("계리사"),
		UNDERWRITER("언더라이터"),
		REAL_ESTATE_ASSET_MANAGER("부동산 자산 관리자"),
		REAL_ESTATE_AGENT("부동산 중개사"),
		APPRAISER("감정평가사"),
		ANALYST("애널리스트"),
		INVESTOR_RELATIONS("IR"),
		TRADER("트레이더"),
		FINANCIAL_ENGINEER("금융공학자"),
		ASSET_MANAGER("자산관리사"),
		INVESTMENT_BANKER("투자은행가"),
		COMPLIANCE_OFFICER("준법감시인"),
		TAX_ACCOUNTANT("세무사"),
		FINANCIAL_MANAGER("재무 담당자"),
		INVESTMENT_SECURITIES("투자/증권"),

		// 제조 / 생산
		ASSEMBLY_TECHNICIAN("조립 기술자"),
		CHEMIST("화학자"),
		MECHANICAL_TECHNICIAN("기계제작 기술자"),
		MECHANICAL_FACILITY_DESIGN("기계/설비/설계"),
		PRODUCTION_MANAGER("생산 관리자"),
		PRODUCTION_WORKER("생산직 종사자"),
		PROCESS_MANAGER("공정 관리자"),
		QUALITY_MANAGER("품질 관리자"),
		SAFETY_MANAGER("안전 관리자"),
		TEST_ENGINEER("제조 테스트 엔지니어"),
		TEXTILES_APPAREL_FASHION("섬유/의류/패션"),
		SEMICONDUCTOR_DISPLAY("반도체/디스플레이"),

		// 의료 / 제작 / 바이오
		DENTAL_HYGIENIST("치과 위생사"),
		PARALEGAL("조무사"),
		CLINICAL_LABORATORY_TECHNOLOGIST("임상병리사"),
		NURSE("간호사"),
		PHYSICAL_THERAPIST("물리 치료사"),
		PHYSICIAN("의사"),
		VETERINARIAN("수의사"),
		NURSE_PARALEGAL("간호 조무사"),
		DENTIST("치과의사"),
		EXAMINER("검안사"),
		PHARMACIST("약사"),
		CLINICAL_TRIAL_RESEARCHER("임상시험 연구원"),
		CLINICAL_TRIAL_NURSE("임상시험 간호사"),
		MICROBIOLOGIST("미생물학자"),
		GENETICIST("유전공학자"),
		RESEARCHER("연구원"),
		ORIENTAL_MEDICINE_PHYSICIAN("한의사"),
		RADIOLOGIST("방사선사"),
		HOSPITAL_COORDINATOR("병원 코디네이터"),
		CLINICAL_PSYCHOLOGIST("임상심리사"),

		// 물류 / 무역
		SHIPPING_HANDLER("배송담당"),
		DIESEL_MECHANIC("디젤 정비공"),
		OPERATIONS_MANAGER("운행 관리원"),
		FORKLIFT_DRIVER("지게차 운전사"),
		DRIVER("운전기사"),
		LOGISTICS_ANALYST("물류 분석가"),
		FREIGHT_TRUCK_DRIVER("화물트럭 운전기사"),
		SHIPPING_CLERK("선적,발송 사무원"),
		TRANSPORTATION_MANAGER("운송 관리자"),
		WAREHOUSE_WORKER("웨어하우스"),
		AIR_TRANSPORTATION("항공 운송"),
		MARINE_TRANSPORTATION("해운/해양 운송"),
		TRADE_OFFICE("무역사무"),
		CUSTOMS_BROKER("관세사"),
		ORIGIN_MANAGEMENT_SPECIALIST("원산지관리사"),
		INBOUND_OUTBOUND_MANAGER("입/출고 관리자"),
		DISTRIBUTION_MANAGER("유통 관리자"),
		MARINE_NAVIGATION_OFFICER("항해사"),
		MECHANICAL_OFFICER("기관사"),

		CURRICULUM_PLANNER("교재/교육기획"),
		PRIVATE_TUTOR("학원강사"),
		SCHOOL_STAFF("교직원"),
		KINDERGARTEN_TEACHER("유치원교사"),
		TEACHER("교사"),
		QUALIFICATION_TECHNICAL_INSTRUCTOR("자격증/기술전문교육"),

		// 식 / 음료
		NUTRITIONIST("영양사"),
		CHEF_BAKER("제빵사"),
		BARTENDER("바텐더"),
		COOK("요리사"),
		FOOD_SERVICE_WORKER("외식업 종사자"),
		RESTAURANT_MANAGER("레스토랑 관리자"),
		FOOD_MD("식품 MD"),
		FOOD_STYLIST("푸드스타일리스트"),
		SOMMELIER("소믈리에"),
		FOOD_PROCESSING_DEVELOPMENT("식품가공/개발"),
		BARISTA("바리스타"),

		// 건설 / 시설
		CARPENTER("목수"),
		HEAVY_EQUIPMENT_TECHNICIAN("중장비 기술자"),
		MAINTENANCE_WORKER("정비공"),
		MAINTENANCE_MANAGER("유지보수 관리자"),
		FACILITY_MANAGER("관리인"),
		ELECTRICIAN("전기 기술자"),
		PLANT_MANAGER("플랜트 관리자"),
		WELDER("용접기사"),
		PLUMBER("배관공"),
		ESTIMATOR("견적 기술자"),
		ARCHITECT("건축가"),
		INTERIOR_ARCHITECT("실내건축"),
		CONSTRUCTION_SUPERVISOR("건축시공/감리"),

		// 공공 / 복지
		COUNSELOR("상담사"),
		FIREFIGHTER("소방관"),
		ENVIRONMENTAL_EXPERT("환경 전문가"),
		INFORMATION_ANALYST("정보 분석가"),
		LIFEGUARD("인명 구조원"),
		REGIONAL_EXPERT("지역 전문가"),
		PUBLIC_OFFICIAL("공무원"),
		PROFESSIONAL_SOLDIER("직업군인"),
		VOLUNTEER("자원봉사자"),
		SOCIAL_WORKER("사회복지사"),
		PARAMEDIC("응급구조사"),
		CAREGIVER("요양보호사"),
		ETC("기타");

		private final String jobName;

		JobName(String jobName) {
			this.jobName = jobName;
		}

		@Override
		public String getName() {
			return this.name();
		}

		@Override
		public String getDescription() {
			return this.jobName;
		}

		@JsonCreator
		public static JobName create(String jobName) {
			return Arrays.stream(values())
				.filter(jobGroup -> Objects.equals(jobGroup.name(), jobName.toUpperCase()))
				.findFirst()
				.orElseThrow(() -> new InvalidArgumentException(jobName, "jobName"));
		}
	}

}
