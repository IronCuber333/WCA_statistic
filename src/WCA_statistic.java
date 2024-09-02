import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WCA_statistic {
    public static void main (String[] args) throws Exception {
        DealData dealing = new DealData ();
        dealing.makePerson ("2021YAMA01");
    }
}

class DealData {

    // WCAからエクスポートしたデータのパス．
    private String pathExportContinent, pathExportCountry, pathExportEligible, pathExportEvent, pathExportFormat, pathExportCompetition, pathExportChampionship, pathExportRoundType, pathExportScramble, pathExportResult, pathExportPerson, pathExportRankSingle, pathExportRankAverage;

    // 作られたデータのパス．
    private String pathPersonRoot;

    private ArrayList<ContinentData> continent; // 大陸の一覧．
    private ArrayList<CountryData> country; // 国の一覧．
    private ArrayList<EligibleData> eligible; // 集合体の一覧．
    private ArrayList<EventData> event; // 種目の一覧．
    private ArrayList<FormatData> format; // フォーマットの一覧．
    private ArrayList<RoundTypeData> roundType; // ラウンドの一覧．

    DealData () {
        // パスの定義．
        pathExportContinent = "data\\WCA_export_Continents.tsv";
        pathExportCountry = "data\\WCA_export_Countries.tsv";
        pathExportEligible = "data\\WCA_export_eligible_country_iso2s_for_championship.tsv";
        pathExportEvent = "data\\WCA_export_Events.tsv";
        pathExportFormat = "data\\WCA_export_Formats.tsv";
        pathExportCompetition = "data\\WCA_export_Competitions.tsv";
        pathExportChampionship = "data\\WCA_export_championships.tsv";
        pathExportRoundType = "data\\WCA_export_RoundTypes.tsv";
        pathExportScramble = "data\\WCA_export_Scrambles.tsv";
        pathExportResult = "data\\WCA_export_Results.tsv";
        pathExportPerson = "data\\WCA_export_Persons.tsv";
        pathExportRankSingle = "data\\WCA_export_RanksSingle.tsv";
        pathExportRankAverage = "data\\WCA_export_RanksAverage.tsv";

        pathPersonRoot = "out\\person\\";
        
        // 大陸の情報．
        {
            continent = new ArrayList<ContinentData> (); // 国情報を格納．
            BufferedReader br = makeBr (pathExportContinent);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("id") == false) { // 1行目でないとき．
                        continent.add (new ContinentData (row)); // 種目情報を格納．
                    }
                }
            }

            // 閉じる．
            closeBr (br);
        }

        // 国の情報．
        {
            country = new ArrayList<CountryData> (); // 国情報を格納．
            BufferedReader br = makeBr (pathExportCountry);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("id") == false) { // 1行目でないとき．
                        country.add (new CountryData (row)); // 種目情報を格納．
                    }
                }
            }

            // 閉じる．
            closeBr (br);
        }

        // 集合体の情報．
        {
            eligible = new ArrayList<EligibleData> (); // 国情報を格納．
            BufferedReader br = makeBr (pathExportEligible);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("championship_type") == false) { // 1行目でないとき．
                        boolean ifExist = false; // 既に集合体データが存在するか．

                        for (int i = 0; i < eligible.size (); i++) {
                            if (eligible.get (i).getEligibleName ().equals (row[0])) { // 集合体名が一致したら．
                                eligible.get (i).addCountry (iso2ToCountry (row[1]));
                                ifExist = true;
                                break;
                            }
                        }

                        if (ifExist == false) {
                            eligible.add (new EligibleData (row[0]));
                            eligible.get (eligible.size () - 1).addCountry (iso2ToCountry (row[1]));
                        }
                    }
                }
            }

            // 閉じる．
            closeBr (br);
        }
        
        // 種目の情報．
        {
            event = new ArrayList<EventData> (); // 種目情報を格納．
            BufferedReader br = makeBr (pathExportEvent);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("id") == false) { // 1行目でないとき．
                        event.add (new EventData (row)); // 種目情報を格納．
                    }
                }
            }

            // 閉じる．
            closeBr (br);

            Collections.sort (event, Comparator.comparingInt ((EventData o) -> o.getEventRank ())); // 種目ランクに応じて並べ替え．

            for (int i = 0; i < event.size (); i++) {
                event.get (i).addEventNumber (i);
            }
        }

        // フォーマットの情報．
        {
            format = new ArrayList<FormatData> (); // フォーマット情報を格納．
            BufferedReader br = makeBr (pathExportFormat);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("id") == false) { // 1行目でないとき．
                        format.add (new FormatData (row)); // フォーマット情報を格納．
                    }
                }
            }

            // 閉じる．
            closeBr (br);
        }

        // ラウンドの情報．
        {
            roundType = new ArrayList<RoundTypeData> (); // フォーマット情報を格納．
            BufferedReader br = makeBr (pathExportRoundType);

            while (true) {
                String[] row = readTsvLine (br); // 元ファイルの情報．

                if (row == null) { // 全て読み切ったらbreak．
                    break;
                }
                else {
                    if (row[0].equals ("id") == false) { // 1行目でないとき．
                        roundType.add (new RoundTypeData (row)); // フォーマット情報を格納．
                    }
                }
            }

            // 閉じる．
            closeBr (br);
        }
    }

    class ContinentData { // 大陸ごとのデータ．
        private String continentId; // 大陸ID．
        private String continentName; // 大陸名．
        private String recordName; // 大陸記録の名前．

        ContinentData (String[] row) { // 大陸シートの行から大陸情報を取得．
            continentId = row[0];
            continentName = row[3];
            recordName = row[4];
        }

        String getContinentId () { // 大陸IDを得る．
            return continentId;
        }

        String getContinentName () { // 大陸名を得る．
            return continentName;
        }

        String getRecordName () { // 大陸記録名を得る．
            return recordName; 
        }
    }

    ContinentData idToContinent (String continentId) { // 国IDから国の情報を得る．
        for (int i = 0; i < continent.size (); i++) {
            if (continent.get (i).getContinentId ().equals (continentId)) { // 大陸IDが一致したら．
                return continent.get (i);
            }
        }

        return null;
    }

    class CountryData { // 国ごとのデータ．
        private String countryId; // 国ID．
        private ContinentData continent; // 大陸ID．
        private String iso2; // iso2．
        private String countryName; // 国名．

        CountryData (String[] row) {
            countryId = row[0];
            continent = idToContinent (row[1]);
            iso2 = row[2];
            countryName = row[3];
        }

        String getCountryId () { // 国IDを得る．
            return countryId;
        }

        ContinentData getContinent () { // 大陸を得る．
            return continent;
        }

        String getIso2 () { // iso2を得る．
            return iso2;
        }

        String getCountryName () { // 国名を得る．
            return countryName;
        }
    }

    CountryData idToCountry (String countryId) { // 国IDから国の情報を取得する．
        for (int i = 0; i < country.size (); i++) {
            if (country.get (i).getCountryId ().equals (countryId)) {
                return country.get (i);
            }
        }

        return null;
    }

    CountryData iso2ToCountry (String iso2) { // iso2から国の情報を取得する．
        for (int i = 0; i < country.size (); i++) {
            if (country.get (i).getIso2 ().equals (iso2)) {
                return country.get (i);
            }
        }

        return null;
    }

    class EligibleData { // 大会以外の，複数の国の集合体に関するデータ．チャンピョンシップ用．
        private String eligibleName;
        private ArrayList<CountryData> countryList;

        EligibleData (String eligibleName) {
            this.eligibleName = eligibleName;
            countryList = new ArrayList<CountryData> ();
        }

        String getEligibleName () { // 集合体の名前を取得する．
            return eligibleName;
        }

        void addCountry (CountryData country) { // 国を追加する．
            countryList.add (country);
        }

        boolean ifIncluded (CountryData country) { // 国があるかどうか確かめる．
            for (int i = 0; i < countryList.size (); i++) {
                if (countryList.get (i) == country) {
                    return true;
                }
            }

            return false;
        }
    }

    EligibleData idToEligible (String eligibleName) { // 集合体名から集合体データを取得する．
        for (int i = 0; i < eligible.size (); i++) {
            if (eligible.get (i).getEligibleName ().equals (eligibleName)) {
                return eligible.get (i);
            }
        }

        return null;
    }

    class EventData { // 種目ごとのデータ．
        private String eventId; // 種目ID．
        private String format; // フォーマット．
        private String eventName; // 種目名．
        private int eventRank; // 種目ランク．
        private int eventNumber; // 種目番号．

        EventData (String[] row) { // 種目シートの行からデータを読みこむ．
            eventId = row[0];
            format = row[1];
            eventName = row[2];
            eventRank = Integer.parseInt (row[3]);
        }

        String getEventId () { // 種目IDを得る．
            return eventId;
        }

        String getFormat () { // フォーマットを得る．
            return format;
        }

        String getEventName () { // 種目名を得る．
            return eventName;
        }

        int getEventRank () { // 種目ランクを得る．
            return eventRank;
        }

        int getEventNumber () { // 種目番号を得る．
            return eventNumber;
        }

        void addEventNumber (int eventNumber) { // 種目番号を登録する．
            this.eventNumber = eventNumber;
        }
    }

    EventData idToEvent (String eventId) { // 種目IDから種目情報を返す．
        for (int i = 0; i < event.size (); i++) {
            if (event.get (i).getEventId ().equals (eventId)) { // 種目IDが一致していたら．
                return event.get (i);
            }
        }

        return null;
    }

    class FormatData { // フォーマットごとのデータ．
        private String formatId; // フォーマットID．
        private int solveCount; // ソルブ数．
        private String formatName; // フォーマット名．
        private String sortBy; // 順位の基準．
        private String sortBySecond; // 順位の基準2．
        private int trimFastest; // 除かれる速いタイム．
        private int trimSlowest; // 除かれる遅いタイム．

        FormatData (String[] row) {
            formatId = row[0];
            solveCount = Integer.parseInt (row[1]);
            formatName = row[2];
            sortBy = row[3];
            sortBySecond = row[4];
            trimFastest = Integer.parseInt (row[5]);
            trimSlowest = Integer.parseInt (row[6]);
        }

        String getFormatId () { // フォーマットIDを得る．
            return formatId;
        }

        int getSolveCount () { // ソルブ数を得る．
            return solveCount;
        }

        String getFormatName () { // フォーマット名を得る．
            return formatName;
        }

        String getSortBy () { // 順位の基準を得る．
            return sortBy;
        }

        String getSortBySecond () { // 順位の基準2を得る．
            return sortBySecond;
        }

        int getTrimFastest () { // 除かれる速いタイムの個数を得る．
            return trimFastest;
        }

        int getTrimSlowest () { // 除かれる遅いタイムの個数を得る．
            return trimSlowest;
        }
    }

    FormatData idToFormat (String formatId) { // フォーマットIDからフォーマット情報を返す．
        for (int i = 0; i < format.size (); i++) {
            if (format.get (i).getFormatId ().equals (formatId)) {
                return format.get (i);
            }
        }

        return null;
    }

    class RoundTypeData { // ラウンドごとのデータ．
        private String roundTypeId; // ラウンドID．
        private boolean ifFinal; // 決勝かどうか．
        private String roundTypeName; // ラウンド名．
        private int roundTypeRank; // ラウンドのランク．

        RoundTypeData (String[] row) {
            roundTypeId = row[0];
            if (row[2].equals ("1")) {
                ifFinal = true;
            }
            else {
                ifFinal = false;
            }
            roundTypeName = row[3];
            roundTypeRank = Integer.parseInt (row[4]);
        }

        String getRoundTypeId () { // ラウンドIDを得る．
            return roundTypeId;
        }

        boolean getIfFinal () { // 決勝かどうか取得する．
            return ifFinal;
        }

        String getRoundTypeName () { // ラウンド名を得る．
            return roundTypeName;
        }

        int getRoundTypeRank () { // ラウンドのランクを得る．
            return roundTypeRank;
        }

    }

    RoundTypeData idToRoundType (String roundTypeId) { // ラウンドIDからラウンド情報を取得．
        for (int i = 0; i < roundType.size (); i++) {
            if (roundType.get (i).getRoundTypeId ().equals (roundTypeId)) {
                return roundType.get (i);
            }
        }

        return null;
    }

    void makePerson (String wcaId) {
        PersonData personData = new PersonData (wcaId);
        personData.makeTsv ();
    }
    
    class PersonData { // 競技者ごとのデータ．
        private String[] personName; // 競技者名．
        private CountryData[] country; // 国籍．
        private String[] gender; // 性別．
        private String[] genderString; // 性別の名称．
        private String wcaId; // WCA ID．

        private ArrayList<ArrayList<ResultData>> result; // 結果．
        private int[] prSingle; // 単発PR．
        private int[] prAverage; // 平均PR．
        private String[] prSingleString; // 単発PRを文字列にしたもの．
        private String[] prAverageString; // 平均PRを文字列にしたもの．
        
        private int[] rankSingleWorld, rankSingleContinent, rankSingleNation; // 単発ランキング．
        private int[] rankAverageWorld, rankAverageContinent, rankAverageNation; // 平均ランキング．

        private int nSolve; // ソルブ数．
        private int[] nPodium; // 表彰台数
        private int nWorldRecord; // 世界記録の数．
        private int nContinentRecord; // 大陸記録の数．
        private int nNationRecord; // 国内記録の数．

        private ArrayList<CompetitionData> competition; // 大会一覧．
        private ArrayList<ArrayList<ResultData>> resultCompetition; // 大会ごとの結果．

        PersonData (String wcaId) { // WCA IDからデータを読みこむ．
            this.wcaId = wcaId;

            ArrayList<String[]> rowPerson = new ArrayList<String[]> (); // WCA IDが一致する行をいったん格納．

            BufferedReader brPerson = makeBr (pathExportPerson);
            while (true) {
                String[] row = readTsvLine (brPerson);

                if (row == null) {
                    break; // 最後まで読んだらbreak;
                }
                else {
                    if (row[4].equals (wcaId)) {
                        rowPerson.add (row);
                    }
                }
            }
            closeBr (brPerson);

            // 競技者名，国籍，性別のデータを格納．
            personName = new String[rowPerson.size ()];
            country = new CountryData[rowPerson.size ()];
            gender = new String[rowPerson.size ()];
            genderString = new String[rowPerson.size ()];
            for (int i = 0; i < rowPerson.size (); i++) {
                int subId = Integer.parseInt (rowPerson.get (i)[0]);
                personName[subId - 1] = rowPerson.get (i)[1];
                country[subId - 1] = idToCountry (rowPerson.get (i)[2]);
                gender[subId - 1] = rowPerson.get (i)[3];
                if (gender[subId - 1].equals ("m")) {
                    genderString[subId - 1] = "Male";
                }
                else if (gender[subId - 1].equals ("f")) {
                    genderString[subId - 1] = "Female";
                }
                else {
                    genderString[subId - 1] = "";
                }
            }

            result = new ArrayList<ArrayList<ResultData>> ();
            for (int i = 0; i < event.size (); i++) {
                result.add (new ArrayList<ResultData> ()); // 各種目の結果リストを初期化．
            }

            nSolve = 0;
            nPodium = new int[3];
            nWorldRecord = 0;
            nContinentRecord = 0;
            nNationRecord = 0;
            competition = new ArrayList<CompetitionData> ();

            BufferedReader brResult = makeBr (pathExportResult);
            while (true) {
                String[] row = readTsvLine (brResult);

                if (row == null) {
                    break; // 最後まで読んだらbreak;
                }
                else {
                    if (row[7].equals (wcaId)) {
                        ResultData newResult = new ResultData (row, personName, country);
                        result.get (newResult.getEvent ().getEventNumber ()).add (newResult); // 種目ごとに仕分けて追加．
                        
                        nSolve += newResult.getNSolve ();

                        // 表彰台を調べる．
                        if (newResult.getRoundType ().getIfFinal ()) { // 決勝のとき．
                            if (ifPodium (newResult.getPosition ()) && (newResult.getSingle () > 0)) { // 記録があって3位以内のとき．
                                nPodium[newResult.getPosition () - 1]++;
                            }
                        }

                        // 地域記録を調べる．
                        if (newResult.getSingleRecord ().equals ("") == false) { // 何かしらの地域記録があるとき．
                            if (newResult.getSingleRecord ().equals ("WR")) {
                                nWorldRecord++;
                            }
                            else if (newResult.getSingleRecord ().equals ("NR")) {
                                nNationRecord++;
                            }
                            else {
                                nContinentRecord++;
                            }
                        }
                        if (newResult.getAverageRecord ().equals ("") == false) { // 何かしらの地域記録があるとき．
                            if (newResult.getAverageRecord ().equals ("WR")) {
                                nWorldRecord++;
                            }
                            else if (newResult.getAverageRecord ().equals ("NR")) {
                                nNationRecord++;
                            }
                            else {
                                nContinentRecord++;
                            }
                        }

                        // 大会がすでに存在するか確認する．
                        boolean ifExist = false;
                        for (int i = 0; i < competition.size (); i++) {
                            if (competition.get (i).getCompetitionId ().equals (newResult.getCompetition ().getCompetitionId ())) { // 大会IDが一致したら．
                                ifExist = true;
                                break;
                            }
                        }

                        if (ifExist == false) { // 存在しなければ．
                            competition.add (newResult.getCompetition ());
                        }
                    }
                }
            }
            closeBr (brResult);
            
            // 大会を並べ替える．
            Collections.sort (competition,
                Comparator.comparingInt ((CompetitionData o) -> o.getYear ()).
                thenComparingInt ((CompetitionData o) -> o.getMonth ()).
                thenComparingInt ((CompetitionData o) -> o.getDay ()).
                thenComparing ((CompetitionData o) -> o.getCompetitionId ())
            );

            for (int i = 0; i < result.size (); i++) {            
                Collections.sort (result.get (i),
                    Comparator.comparingInt ((ResultData o) -> o.getCompetition ().getYear ()).
                    thenComparingInt ((ResultData o) -> o.getCompetition ().getMonth ()).
                    thenComparingInt ((ResultData o) -> o.getCompetition ().getDay ()).
                    thenComparing ((ResultData o) -> o.getCompetition ().getCompetitionName ()).
                    thenComparingInt ((ResultData o) -> o.getRoundType ().getRoundTypeRank ())
                ); // 大会順に並べ替える．
            }

            // PRを求める．
            prSingle = new int[result.size ()];
            prAverage = new int[result.size ()];
            prSingleString = new String[result.size ()];
            prAverageString = new String[result.size ()];

            for (int i = 0; i < result.size (); i++) {

                prSingle[i] = 0; // 現時点での単発PR．
                prAverage[i] = 0; // 現時点での平均PR．
                prSingleString[i] = "";
                prAverageString[i] = "";

                for (int j = 0; j < result.get (i).size (); j++) {
                    // 単発．
                    if (
                        ((result.get (i).get (j).getSingle () > 0) && (result.get (i).get (j).getSingle () < prSingle[i])) ||  // 元々記録を持っていて更新した場合．
                        ((prSingle[i] == 0) && (result.get (i).get (j).getSingle () > 0)) // 元々記録を持っていなくて記録を残した場合．
                    ) {
                        result.get (i).get (j).prSingle ();
                        prSingle[i] = result.get (i).get (j).getSingle ();
                        prSingleString[i] = result.get (i).get (j).getSingleString ();
                    }

                    // 平均．
                    if (
                        ((result.get (i).get (j).getAverage () > 0) && (result.get (i).get (j).getAverage () < prAverage[i])) || // 元々記録を持っていて更新した場合．
                        ((prAverage[i] == 0) && (result.get (i).get (j).getAverage () > 0)) // 元々記録を持っていなくて記録を残した場合．
                    ) {
                        result.get (i).get (j).prAverage ();
                        prAverage[i] = result.get (i).get (j).getAverage ();
                        prAverageString[i] = result.get (i).get (j).getAverageString ();
                    }
                }
            }
            
            // ランキングを求める．
            rankSingleWorld = new int[event.size ()];
            rankSingleContinent = new int[event.size ()];
            rankSingleNation = new int[event.size ()];
            rankAverageWorld = new int[event.size ()];
            rankAverageContinent = new int[event.size ()];
            rankAverageNation = new int[event.size ()];

            BufferedReader brRankSingle = makeBr (pathExportRankSingle);

            while (true) {
                String[] row = readTsvLine (brRankSingle);
                if (row == null) {
                    break;
                }
                else {
                    if (row[0].equals (wcaId)) { // WCA IDが一致したら．
                        int eventNumber = idToEvent (row[1]).getEventNumber ();
                        rankSingleWorld[eventNumber] = Integer.parseInt (row[3]);
                        rankSingleContinent[eventNumber] = Integer.parseInt (row[4]);
                        rankSingleNation[eventNumber] = Integer.parseInt (row[5]);
                    }
                }
            }

            closeBr (brRankSingle);

            BufferedReader brRankAverage = makeBr (pathExportRankAverage);

            while (true) {
                String[] row = readTsvLine (brRankAverage);
                if (row == null) {
                    break;
                }
                else {
                    if (row[0].equals (wcaId)) { // WCA IDが一致したら．
                        int eventNumber = idToEvent (row[1]).getEventNumber ();
                        rankAverageWorld[eventNumber] = Integer.parseInt (row[3]);
                        rankAverageContinent[eventNumber] = Integer.parseInt (row[4]);
                        rankAverageNation[eventNumber] = Integer.parseInt (row[5]);
                    }
                }
            }

            closeBr (brRankAverage);

            // 大会ごとの結果を求める．
            resultCompetition = new ArrayList<ArrayList<ResultData>> ();
            for (int i = 0; i < competition.size (); i++) {
                resultCompetition.add (new ArrayList<ResultData> ());
            }

            for (int i = 0; i < result.size (); i++) {
                for (int j = 0; j < result.get (i).size (); j++) {
                    for (int k = 0; k < competition.size (); k++) {
                        if (competition.get (k).getCompetitionId ().equals (result.get (i).get (j).getCompetition ().getCompetitionId ())) { // 大会IDが一致したら．
                            resultCompetition.get (k).add (result.get (i).get (j)); // 結果を大会ごとに分類して追加．
                            break;
                        }
                    }
                }
            }
        }

        void makeTsv () { // 競技者のtsvを作る．
            BufferedWriter bw = makeBw (pathPersonRoot + wcaId + ".tsv");
            
            // WCA ID．
            writeTsvLine (bw, new String[] {"WCA ID", wcaId});

            String[] rowNameTitle = new String[3 * personName.length];
            String[] rowName = new String[3 * personName.length]; // 名前などの情報が格納された行．

            for (int i = 0; i < personName.length; i++) {
                rowNameTitle[3 * i] = "Name";
                rowNameTitle[3 * i + 1] = "Region";
                rowNameTitle[3 * i + 2] = "Gender";
                rowName[3 * i] = personName[i];
                rowName[3 * i + 1] = country[i].getCountryName ();
                rowName[3 * i + 2] = genderString[i];
            }
            writeTsvLine (bw, rowNameTitle);
            writeTsvLine (bw, rowName);

            // 大会数．
            writeTsvLine (bw, new String[] {"Competition", Integer.toString (competition.size ())});

            // ソルブ数．
            writeTsvLine (bw, new String[] {"Completed Solves", Integer.toString (nSolve)});

            writeTsvLine (bw, new String[] {});

            // PR．
            writeTsvLine (bw, new String[] {"Event", "NR", "CR", "WR", "Single", "Average", "WR", "CR", "NR"});
            for (int i = 0; i < event.size (); i++) {
                writeTsvLine (bw, new String[] {
                    event.get (i).getEventName (),
                    rankToString (rankSingleNation[i]),
                    rankToString (rankSingleContinent[i]),
                    rankToString (rankSingleWorld[i]),
                    prSingleString[i],
                    prAverageString[i],
                    rankToString (rankAverageWorld[i]),
                    rankToString (rankAverageContinent[i]),
                    rankToString (rankAverageNation[i])
                });
            }

            writeTsvLine (bw, new String[] {});

            // 表彰台の数．

            writeTsvLine (bw, new String[] {"Gold", "Silver", "Bronze"});
            writeTsvLine (bw, new String[] {
                Integer.toString (nPodium[0]),
                Integer.toString (nPodium[1]),
                Integer.toString (nPodium[2])
            });

            writeTsvLine (bw, new String[] {});

            // 地域記録の数．
            writeTsvLine (bw, new String[] {"WR", "CR", "NR"});
            writeTsvLine (bw, new String[] {
                Integer.toString (nWorldRecord),
                Integer.toString (nContinentRecord),
                Integer.toString (nNationRecord)
            });

            writeTsvLine (bw, new String[] {});

            // 結果．
            writeTsvLine (bw, new String[] {"Competition", "Round", "Place", "", "", "Single", "Average", "", "", "Solves"}); // 種目ID．
            for (int i = 0; i < result.size (); i++) {
                writeTsvLine (bw, new String[] {event.get (i).getEventName ()}); // 種目ID．
                for (int j = 0; j < result.get (i).size (); j++) {

                    writeTsvLine (bw, new String[] {
                        result.get (i).get (j).getCompetition ().getCompetitionName (),
                        result.get (i).get (j).getRoundType ().getRoundTypeName (),
                        Integer.toString (result.get (i).get (j).getPosition ()),
                        result.get (i).get (j).getSingleRecord (),
                        result.get (i).get (j).getPrSingle (),
                        result.get (i).get (j).getSingleString (),
                        result.get (i).get (j).getAverageString (),
                        result.get (i).get (j).getPrAverage (),
                        result.get (i).get (j).getAverageRecord (),
                        result.get (i).get (j).getValueString ()[0],
                        result.get (i).get (j).getValueString ()[1],
                        result.get (i).get (j).getValueString ()[2],
                        result.get (i).get (j).getValueString ()[3],
                        result.get (i).get (j).getValueString ()[4]
                    });
                }
            }

            writeTsvLine (bw, new String[] {});

            // 大会ごと．
            writeTsvLine (bw, new String[] {"Event", "Round", "Place", "", "", "Single", "Average", "", "", "Solves"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                writeTsvLine (bw, new String[] {competition.get (i).getCompetitionName ()}); // 大会名．
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {

                    writeTsvLine (bw, new String[] {
                        resultCompetition.get (i).get (j).getEvent ().getEventName (),
                        resultCompetition.get (i).get (j).getRoundType ().getRoundTypeName (),
                        Integer.toString (resultCompetition.get (i).get (j).getPosition ()),
                        resultCompetition.get (i).get (j).getSingleRecord (),
                        resultCompetition.get (i).get (j).getPrSingle (),
                        resultCompetition.get (i).get (j).getSingleString (),
                        resultCompetition.get (i).get (j).getAverageString (),
                        resultCompetition.get (i).get (j).getPrAverage (),
                        resultCompetition.get (i).get (j).getAverageRecord (),
                        resultCompetition.get (i).get (j).getValueString ()[0],
                        resultCompetition.get (i).get (j).getValueString ()[1],
                        resultCompetition.get (i).get (j).getValueString ()[2],
                        resultCompetition.get (i).get (j).getValueString ()[3],
                        resultCompetition.get (i).get (j).getValueString ()[4]
                    });
                }
            }

            writeTsvLine (bw, new String[] {});

            // 表彰台．
            writeTsvLine (bw, new String[] {"Competition", "Event", "Place", "Single", "Average", "Solves"});

            writeTsvLine (bw, new String[] {"Podiums"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {
                    if (ifPodium (resultCompetition.get (i).get (j).getPosition ())) { // 順位が3位以内ならば．
                        writeTsvLine (bw, new String[] {
                            resultCompetition.get (i).get (j).getCompetition ().getCompetitionName (),
                            resultCompetition.get (i).get (j).getEvent ().getEventName (),
                            Integer.toString (resultCompetition.get (i).get (j).getPosition ()),
                            resultCompetition.get (i).get (j).getSingleString (),
                            resultCompetition.get (i).get (j).getAverageString (),
                            resultCompetition.get (i).get (j).getValueString ()[0],
                            resultCompetition.get (i).get (j).getValueString ()[1],
                            resultCompetition.get (i).get (j).getValueString ()[2],
                            resultCompetition.get (i).get (j).getValueString ()[3],
                            resultCompetition.get (i).get (j).getValueString ()[4],
                        });
                    }
                }
            }

            writeTsvLine (bw, new String[] {});

            // 地域記録．
            writeTsvLine (bw, new String[] {"Competition", "Event", "Round", "Single", "Average", "Solves"});

            // 世界記録．
            writeTsvLine (bw, new String[] {"History of World Records"});
            for (int i = 0; i < result.size (); i++) {
                for (int j = 0; j < result.get (i).size (); j++) {
                    boolean ifSingle = result.get (i).get (j).getSingleRecord ().equals ("WR"); // 単発WRかどうか．
                    boolean ifAverage = result.get (i).get (j).getAverageRecord ().equals ("WR"); // 平均WRかどうか．

                    if (ifSingle || ifAverage) {
                        writeTsvLine (bw, rowRecord (result.get (i).get (j), ifSingle, ifAverage));
                    }
                }
            }
            
            // 大陸記録．
            writeTsvLine (bw, new String[] {"History of Continental Records"});
            for (int i = 0; i < result.size (); i++) {
                for (int j = 0; j < result.get (i).size (); j++) {
                    boolean ifSingle = (
                        (result.get (i).get (j).getSingleRecord ().equals ("") == false) &&
                        (result.get (i).get (j).getSingleRecord ().equals ("WR") == false) &&
                        (result.get (i).get (j).getSingleRecord ().equals ("NR") == false)); // 単発CRかどうか．
                    boolean ifAverage = (
                        (result.get (i).get (j).getAverageRecord ().equals ("") == false) &&
                        (result.get (i).get (j).getAverageRecord ().equals ("WR") == false) &&
                        (result.get (i).get (j).getAverageRecord ().equals ("NR") == false)); // 平均CRかどうか．

                    if (ifSingle || ifAverage) {
                        writeTsvLine (bw, rowRecord (result.get (i).get (j), ifSingle, ifAverage));
                    }
                }
            }

            // 国内記録．
            writeTsvLine (bw, new String[] {"History of National Records"});
            for (int i = 0; i < result.size (); i++) {
                for (int j = 0; j < result.get (i).size (); j++) {
                    boolean ifSingle = result.get (i).get (j).getSingleRecord ().equals ("NR"); // 単発NRかどうか．
                    boolean ifAverage = result.get (i).get (j).getAverageRecord ().equals ("NR"); // 平均NRかどうか．

                    if (ifSingle || ifAverage) {
                        writeTsvLine (bw, rowRecord (result.get (i).get (j), ifSingle, ifAverage));
                    }
                }
            }

            writeTsvLine (bw, new String[] {});

            // チャンピョンシップ．
            writeTsvLine (bw, new String[] {"Competition", "Event", "Place", "Single", "Average", "Solves"});

            // 世界大会．
            writeTsvLine (bw, new String[] {"World Championship Podiums"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {
                    for (int k = 0; k < resultCompetition.get (i).get (j).getChampionshipRange ().length; k++) {
                        if (resultCompetition.get (i).get (j).getChampionshipRange ()[k].equals ("world")) { // 範囲が世界ならば．
                            if (ifPodium (resultCompetition.get (i).get (j).getChampionshipPosition ()[k])) { // 順位が3位以内ならば．
                                writeTsvLine (bw, rowChampionship (resultCompetition.get (i).get (j), k));
                            }
                        }
                    }
                }
            }

            // 大陸大会．
            writeTsvLine (bw, new String[] {"Continental Championship Podiums"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {
                    for (int k = 0; k < resultCompetition.get (i).get (j).getChampionshipRange ().length; k++) {
                        if (resultCompetition.get (i).get (j).getChampionshipRange ()[k].equals ("continent")) { // 範囲が大陸ならば．
                            if (ifPodium (resultCompetition.get (i).get (j).getChampionshipPosition ()[k])) { // 順位が3位以内ならば．
                                writeTsvLine (bw, rowChampionship (resultCompetition.get (i).get (j), k));
                            }
                        }
                    }
                }
            }

            // 中国地域大会．
            writeTsvLine (bw, new String[] {"Greater China Championship Podiums"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {
                    for (int k = 0; k < resultCompetition.get (i).get (j).getChampionshipRange ().length; k++) {
                        if (resultCompetition.get (i).get (j).getChampionshipRange ()[k].equals ("eligible")) { // 範囲が集合体ならば．
                            if (ifPodium (resultCompetition.get (i).get (j).getChampionshipPosition ()[k])) { // 順位が3位以内ならば．
                                writeTsvLine (bw, rowChampionship (resultCompetition.get (i). get(j), k));
                            }
                        }
                    }
                }
            }

            // 国別大会．
            writeTsvLine (bw, new String[] {"National Championship Podiums"});
            for (int i = 0; i < resultCompetition.size (); i++) {
                for (int j = 0; j < resultCompetition.get (i).size (); j++) {
                    for (int k = 0; k < resultCompetition.get (i).get (j).getChampionshipRange ().length; k++) {
                        if (resultCompetition.get (i).get (j).getChampionshipRange ()[k].equals ("country")) { // 範囲が国ならば．
                            if (ifPodium (resultCompetition.get (i).get (j).getChampionshipPosition ()[k])) { // 順位が3位以内ならば．
                                writeTsvLine (bw, rowChampionship (resultCompetition.get (i).get (j), k));
                            }
                        }
                    }
                }
            }
            
            closeBw (bw);
        }
    }

    class CompetitionData { // 大会ごとのデータ．
        private String competitionId; // 大会ID．
        private String competitionName; // 大会名．
        private CountryData country; // 国．
        private boolean cancelled; // キャンセルされたかどうか．
        private int year; // 開催年．
        private int month; // 開催月．
        private int day; // 開催日．

        private ArrayList<String> championshipType; // チャンピョンシップのタイプ．

        CompetitionData (String competitionId) {
            this.competitionId = competitionId;

            BufferedReader brCompetition = makeBr (pathExportCompetition);

            while (true) {
                String[] row = readTsvLine (brCompetition);

                if (row == null) {
                    break; // 最後まで読み切ったらbreak．
                }
                else {
                    if (row[0].equals (competitionId)) { // 大会IDが一致するか．
                        competitionName = row[1];
                        country = idToCountry (row[3]);
                        if (row[12].equals ("1")) {
                            cancelled = true;
                        }
                        else {
                            cancelled = false;
                        }
                        year = Integer.parseInt (row[16]);
                        month = Integer.parseInt (row[17]);
                        day = Integer.parseInt (row[18]);

                        break;
                    }
                }
            }

            closeBr (brCompetition);

            // チャンピョンシップ．
            championshipType = new ArrayList<String> (); // チャンピョンシップ情報を一時的に格納．
            BufferedReader brChampionship = makeBr (pathExportChampionship);

            while (true) {
                String[] row = readTsvLine (brChampionship);

                if (row == null) {
                    break; // 全て読み切ったらbreak．
                }
                else {
                    if (row[1].equals (competitionId)) { // 大会IDが一致したら．
                        championshipType.add (row[2]);
                    }
                }
            }

            closeBr (brChampionship);
        }

        String getCompetitionId () { // 大会IDを得る．
            return competitionId;
        }

        String getCompetitionName () { // 大会名を得る．
            return competitionName;
        }

        CountryData getCountry () { // 開催国を得る．
            return country;
        }

        boolean getCancelled () { // キャンセルされたかどうかを得る．
            return cancelled;
        }

        int getYear () { // 開催年を得る．
            return year;
        }

        int getMonth () { // 開催月を得る．
            return month;
        }

        int getDay () { // 開催日を得る．
            return day;
        }

        ArrayList<String> getChampionshipType () { // チャンピョンシップを得る．
            return championshipType;
        }
    }

    class ResultData { // 結果ごとのデータ．
        private CompetitionData competition; // 大会．
        private EventData event; // 種目．
        private RoundTypeData roundType; // ラウンドID．
        private int position; // 順位．
        private int single; // 単発記録．
        private int average; // 平均記録．
        private String wcaId; // WCA ID．
        private FormatData format; // フォーマット．
        private String singleRecord; // 単発の地域記録．
        private String averageRecord; // 平均の地域記録．
        private int[] value; // 内訳．
        private CountryData country; // 国．

        private int subId; // sub ID．

        private String singleString; // 単発記録の文字列．
        private String averageString; // 平均記録の文字列．
        private String[] valueString; // 内訳の文字列．

        private int nSolve; // ソルブ数．

        private String prSingle; // 単発PR．
        private String prAverage; // 平均PR．

        private String[] championshipRange; // チャンピョンシップの範囲．"world"，"continent"，"country"，"eligible"のいずれか．
        private int[] championshipPosition; // チャンピョンシップでの順位．

        ResultData (String[] row, String[] personName, CountryData[] countryList) { // 結果シートの行からデータを読みこむ．
            competition = new CompetitionData (row[0]);
            event = idToEvent (row[1]);
            roundType = idToRoundType (row[2]);
            position = Integer.parseInt (row[3]);
            single = Integer.parseInt (row[4]);
            average = Integer.parseInt (row[5]);
            wcaId = row[7];
            format = idToFormat (row[8]);
            value = new int[5];
            for (int i = 0; i < 5; i++) {
                value[i] = Integer.parseInt (row[i + 9]);
            }
            if (row[14].equals ("NULL")) {
                singleRecord = "";
            }
            else {
                singleRecord = row[14];
            }
            if (row[15].equals ("NULL")) {
                averageRecord = "";
            }
            else {
                averageRecord = row[15];
            }

            // subIdを求める．
            subId = 1;
            this.country = null;
            for (int i = 0; i < personName.length; i++) {
                if ((personName[i].equals (row[6])) && (countryList[i].getCountryId ().equals (row[16]))) {
                    subId = i + 1;
                    country = countryList[i];
                    break;
                }
            }

            // 各記録を文字列に直したものを求める．

            valueString = new String[5];

            if (event.getFormat ().equals ("time")) { // 通常のタイムのフォーマット．
                singleString = timeToString (single);
                averageString = timeToString (average);

                for (int i = 0; i < 5; i++) {
                    valueString[i] = timeToString (value[i]);
                }
            }
            else if (event.getFormat ().equals ("number")) { // FMCのフォーマット．
                singleString = fmcSingleToString (single);
                averageString = fmcAverageToString (average);
                valueString = new String[5];

                for (int i = 0; i < 5; i++) {
                    valueString[i] = fmcSingleToString (value[i]);
                }
            }
            else { // MBLDのフォーマット．
                singleString = multiToString (single);
                averageString = "";

                for (int i = 0; i < 5; i++) {
                    valueString[i] = multiToString (value[i]);
                }
            }

            // 除く場合，速いタイムと遅いタイムにかっこをつける．
            if (value[format.getSolveCount () - 1] != 0) { // ソルブが全て埋まっている場合．
                int[] modifiedValue = new int[format.getSolveCount ()]; // 有効な試技のみの内訳．
                for (int i = 0; i < format.getSolveCount (); i++) {
                    modifiedValue[i] = value[i];
                }
                int[] sortedNumber = sortWcaFormat (modifiedValue); // 試技番号を速い順に並べ替えたもの．
            
                for (int i = 0; i < format.getTrimFastest (); i++) {
                    valueString[sortedNumber[i]] = "(" + valueString[sortedNumber[i]] + ")";
                }
                for (int i = 0; i < format.getTrimSlowest (); i++) {
                    valueString[sortedNumber[sortedNumber.length - i - 1]] = "(" + valueString[sortedNumber[sortedNumber.length - i - 1]] + ")";
                }
            }

            // ソルブ数を求める．
            nSolve = 0;
            for (int i = 0; i < 5; i++) {
                if (value[i] > 0) {
                    nSolve++;
                }
            }

            prSingle = "";
            prAverage = "";

            // チャンピョンシップのデータを求める．
            
            if ((roundType.getIfFinal ()) && (single > 0)) { // 決勝で記録があれば．
                ArrayList<String> championshipType = competition.getChampionshipType ();
                championshipRange = new String[championshipType.size ()];
                championshipPosition = new int[championshipType.size ()];

                for (int i = 0; i < championshipType.size (); i++) {
                    if (championshipType.get (i).equals ("world")) { // 世界の場合．
                        championshipPosition[i] = position;
                        championshipRange[i] = "world";
                    }
                    else {
                        // 大陸別か確かめる．
                        ContinentData continentChampionship = idToContinent (championshipType.get (i));
                        if (continentChampionship != null) { // 大陸ならば．
                            championshipRange[i] = "continent";

                            if (continentChampionship == country.getContinent ()) { // 大陸が一致すれば．
                                int countGreater = 0; // 良い記録を数える．

                                BufferedReader br = makeBr (pathExportResult);

                                while (true) {
                                    String[] rowResult = readTsvLine (br);

                                    if (rowResult == null) {
                                        break; // 全て読み切ったらbreak．
                                    }
                                    else {
                                        if (
                                            rowResult[0].equals (competition.getCompetitionId ()) &&
                                            rowResult[1].equals (event.getEventId ()) &&
                                            rowResult[2].equals (roundType.getRoundTypeId ())
                                        ) { // 大会，種目，ラウンドが一致するならば．
                                            if (Integer.parseInt (rowResult[3]) < position) { // 順位が良い場合．
                                                if (idToCountry (rowResult[16]).getContinent () == continentChampionship) { // 大陸が一致したら．
                                                    countGreater++;
                                                }
                                            }
                                        }
                                    }
                                }

                                closeBr (br);
                                championshipPosition[i] = countGreater + 1;
                            }
                        }
                        else { // 大陸ではない場合．
                            // 国別か確かめる．
                            CountryData countryChampionship = iso2ToCountry (championshipType.get (i));
                            if (countryChampionship != null) { // 国ならば．
                                championshipRange[i] = "country";

                                if (countryChampionship == country) { // 国が一致するならば．
                                    int countGreater = 0; // 良い記録を数える．

                                    BufferedReader br = makeBr (pathExportResult);

                                    while (true) {
                                        String[] rowResult = readTsvLine (br);

                                        if (rowResult == null) {
                                            break; // 全て読み切ったらbreak．
                                        }
                                        else {
                                            if (
                                                rowResult[0].equals (competition.getCompetitionId ()) &&
                                                rowResult[1].equals (event.getEventId ()) &&
                                                rowResult[2].equals (roundType.getRoundTypeId ())
                                            ) { // 大会，種目，ラウンドが一致するならば．
                                                if (Integer.parseInt (rowResult[3]) < position) { // 順位が良い場合．
                                                    if (rowResult[16].equals (countryChampionship.getCountryId ())) { // 国IDが一致したら．
                                                        countGreater++;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    closeBr (br);
                                    championshipPosition[i] = countGreater + 1;
                                }
                            }
                            else { // 他はeligible．
                                EligibleData eligibleChampionship = idToEligible (championshipType.get (i));
                                championshipRange[i] = "eligible";

                                if (eligibleChampionship.ifIncluded (country)) { // 国が含まれていたら．
                                    int countGreater = 0; // 良い記録を数える．

                                    BufferedReader br = makeBr (pathExportResult);

                                    while (true) {
                                        String[] rowResult = readTsvLine (br);

                                        if (rowResult == null) {
                                            break; // 全て読み切ったらbreak．
                                        }
                                        else {
                                            if (
                                                rowResult[0].equals (competition.getCompetitionId ()) &&
                                                rowResult[1].equals (event.getEventId ()) &&
                                                rowResult[2].equals (roundType.getRoundTypeId ())
                                            ) { // 大会，種目，ラウンドが一致するならば．
                                                if (Integer.parseInt (rowResult[3]) < position) { // 順位が良い場合．
                                                    if (eligibleChampionship.ifIncluded (idToCountry (rowResult[16]))) { // 国が集合に含まれていたら．
                                                        countGreater++;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    closeBr (br);
                                    championshipPosition[i] = countGreater + 1;
                                }
                            }
                        }
                    }
                }
            }
            else { // 決勝でないとき．
                championshipRange = new String[0];
                championshipPosition = new int[0];
            }
        }

        CompetitionData getCompetition () { // 大会データを得る．
            return competition;
        }

        EventData getEvent () { // 種目データを得る．
            return event;
        }

        RoundTypeData getRoundType () { // ラウンドデータを得る．
            return roundType;
        }
        
        int getPosition () { // 順位を得る．
            return position;
        }

        int getSingle () { // 単発記録を得る．
            return single;
        }

        int getAverage () { // 平均記録を得る．
            return average;
        }
        
        String getWcaId () { // WCA IDを得る．
            return wcaId;
        }
        
        FormatData getFormat () { // フォーマットを得る．
            return format;
        }

        String getSingleRecord () { // 単発地域記録を得る
            return singleRecord;
        }
        
        String getAverageRecord () { // 平均地域記録を得る．
            return averageRecord;
        }
        
        int[] getValue () { // 内訳を得る．
            return value;
        }

        CountryData getCountry () { // 国を得る．
            return country;
        }

        int getSubId () { // sub IDを得る．
            return subId;
        }

        String getSingleString () { // 単発記録の文字列を得る．
            return singleString;
        }

        String getAverageString () { // 平均記録の文字列を得る．
            return averageString;
        }

        String[] getValueString () { // 内訳の文字列を得る．
            return valueString;
        }

        int getNSolve () { // ソルブ数を得る．
            return nSolve;
        }

        String getPrSingle () { // 単発PRを得る．
            return prSingle;
        }

        String getPrAverage () { // 平均PRを得る．
            return prAverage;
        }

        void prSingle () {
            prSingle = "PR"; // 単発PRにする．
        }

        void prAverage () {
            prAverage = "PR"; // 平均PRにする．
        }

        int[] getChampionshipPosition () { // チャンピョンシップの種類を得る．
            return championshipPosition;
        }

        String[] getChampionshipRange () { // チャンピョンシップの範囲を得る．
            return championshipRange;
        }
    }

    private String timeToString (int time) { // タイムを文字列に変える補助関数．
        if (time == 0) {
            return "";
        }
        else if (time == -1) {
            return "DNF";
        }
        else if (time == -2) {
            return "DNS";
        }
        else {
            int hour = time / 360000; // 時間．
            int minute = (time % 360000) / 6000; // 分．
            int second = (time % 6000) / 100; // 秒．
            int decimal = time % 100; // 小数点以下．

            // 各部分の文字列．
            String hourString, minuteString, secondString, decimalString;

            // 時間．
            if (hour == 0) { // 時間の桁がないとき．
                hourString = "";
            }
            else {
                hourString = Integer.toString (hour) + ":";
            }

            // 分．
            if (hour == 0) {
                if (minute == 0) { // 時間の桁も分の桁もないとき．
                    minuteString = "";
                }
                else {
                    minuteString = Integer.toString (minute) + ":";
                }
            }
            else {
                if (minute < 10) { // 分が1桁のとき．
                    minuteString = "0" + Integer.toString (minute) + ":";
                }
                else {
                    minuteString = Integer.toString (minute) + ":";
                }
            }

            // 秒．
            if ((hour == 0) && (minute == 0)) { // 時間の桁も分の桁もないとき．
                secondString = Integer.toString (second) + ".";
            }
            else {
                if (second < 10) { // 秒が1桁のとき．
                    secondString = "0" + Integer.toString (second) + ".";
                }
                else {
                    secondString = Integer.toString (second) + ".";
                }
            }

            // 小数点以下．
            if (decimal < 10) { // 小数点以下が1桁のとき．
                decimalString = "0" + Integer.toString (decimal);
            }
            else {
                decimalString = Integer.toString (decimal);
            }

            return hourString + minuteString + secondString + decimalString;
        }
    }

    private String timeWithOutDecimal (int time) { // タイムを文字列に変える補助関数．ただし小数点以下はなし．
        if (time == 99999) {
            return "?:??:??";
        }
        else {
            int hour = time / 3600; // 時間．
            int minute = (time % 3600) / 60; // 分．
            int second = time % 60; // 秒．

            // 各部分の文字列．
            String hourString, minuteString, secondString;

            // 時間．
            if (hour == 0) { // 時間の桁がないとき．
                hourString = "";
            }
            else {
                hourString = Integer.toString (hour) + ":";
            }

            // 分．
            if (hour == 0) { // 時間の桁がないとき． 
                minuteString = Integer.toString (minute) + ":";
            }
            else {
                if (minute < 10) { // 分が1桁のとき．
                    minuteString = "0" + Integer.toString (minute) + ":";
                }
                else {
                    minuteString = Integer.toString (minute) + ":";
                }
            }

            // 秒．
            if (second < 10) { // 秒が1桁のとき．
                secondString = "0" + Integer.toString (second);
            }
            else {
                secondString = Integer.toString (second);
            }
            return hourString + minuteString + secondString;
        }
    }

    private String fmcSingleToString (int fmcSingle) { // FMCの単発記録を文字列に変える補助関数．
        if (fmcSingle == 0) {
            return "";
        }
        else if (fmcSingle == -1) {
            return "DNF";
        }
        else if (fmcSingle == -2) {
            return "DNS";
        }
        else {
            return Integer.toString (fmcSingle);
        }
    }

    private String fmcAverageToString (int fmcAverage) { // FMCの平均記録を文字列に変える補助関数．
        if (fmcAverage == 0) {
            return "";
        }
        else if (fmcAverage == -1) {
            return "DNF";
        }
        else if (fmcAverage == -2) {
            return "DNS";
        }
        else {
            int moveCount = fmcAverage / 100;
            int decimal = fmcAverage % 100;

            if (decimal < 10) { // 小数点以下が1桁のとき．
                return Integer.toString (moveCount) + ".0" + Integer.toString (decimal);
            }
            else {
                return Integer.toString (moveCount) + "." + Integer.toString (decimal);
            }
        }
    }

    private String multiToString (int multi) { // MBLDの記録を文字列に変える補助関数．
        if (multi == 0) {
            return "";
        }
        else if (multi == -1) {
            return "DNF";
        }
        else if (multi == -2) {
            return "DNS";
        }
        else {
            int success; // 成功個数．
            int challenge; // 挑戦個数．
            int time; // タイム．

            if (multi < 1000000000) { // 現代の形式．
                int point = 99 - (multi / 10000000); // ポイント．
                int fail = multi % 100; // 失敗個数．
                success = point + fail;
                challenge = success + fail;
                time = (multi % 10000000) / 100;
            }
            else { // 古代の形式．
                success = 199 - (multi / 10000000);
                challenge = (multi % 10000000) / 100000;
                time = multi % 100000;
            }

            String timeString = timeWithOutDecimal (time); // タイムを文字列にしたもの．

            return Integer.toString (success) + "/" + Integer.toString (challenge) + " " + timeString;
        }
    }

    private int[] sortWcaFormat (int[] value) { // WCAのフォーマットにおいて良い順に並べ替えたものを求める．        
        
        ArrayList<ValueNumberPair> solvedPair = new ArrayList<ValueNumberPair> (); // 記録がある試技の一覧．
        boolean[] solved = new boolean[value.length]; // 各試技が揃っているかどうか．

        for (int i = 0; i < value.length; i++) {
            if (value[i] > 0) { // 記録なしでなければ追加．
                solvedPair.add (new ValueNumberPair (value[i], i));
                solved[i] = true;
            }
        }

        Collections.sort (solvedPair, Comparator.comparingInt ((ValueNumberPair o) -> o.getValue ()));

        int[] sortedNumber = new int[value.length]; // ソートされた試技番号．

        for (int i = 0; i < solvedPair.size (); i++) {
            sortedNumber[i] = solvedPair.get (i).getNumber ();
        }
        int currentNumber = solvedPair.size (); // sortedNumberに現在入れるべき番号．
        for (int i = 0; i < value.length; i++) {
            if (solved[i] == false) {
                sortedNumber[currentNumber] = i;
                currentNumber++;
            }
        }

        return sortedNumber;   
    }

    class ValueNumberPair { // 記録と試技番号のペア．
        private int value;
        private int number;
        
        ValueNumberPair (int value, int number) {
            this.value = value;
            this.number = number;
        }

        int getValue () {
            return value;
        }

        int getNumber () {
            return number;
        }
    }

    String rankToString (int rank) { // ランキングを文字列にする．
        if (rank == 0) {
            return "";
        }
        else {
            return Integer.toString (rank);
        }
    }

    private boolean ifPodium (int position) {
        if ((position >= 1) && (position <= 3)) {
            return true;
        }
        else {
            return false;
        }
    }

    private String[] rowRecord (ResultData result, boolean ifSingle, boolean ifAverage) { // 地域記録のフォーマット．
        String singleString, averageString;
        String[] valueString;

        if (ifSingle) {
            singleString = result.getSingleString ();
        }
        else {
            singleString = "";
        }
        if (ifAverage) {
            averageString = result.getAverageString ();
            valueString = result.getValueString ();
        }
        else {
            averageString = "";
            valueString = new String[] {"", "", "", "", ""};
        }
        
        return new String[] {
            result.getCompetition ().getCompetitionName (),
            result.getEvent ().getEventName (),
            result.getRoundType ().getRoundTypeName (),
            singleString,
            averageString,
            valueString [0],
            valueString [1],
            valueString [2],
            valueString [3],
            valueString [4],
        };
    }

    String[] rowChampionship (ResultData result, int k) { // チャンピョンシップ入賞のフォーマット．
        return new String[] {
            result.getCompetition ().getCompetitionName (),
            result.getEvent ().getEventName (),
            Integer.toString (result.getChampionshipPosition ()[k]),
            result.getSingleString (),
            result.getAverageString (),
            result.getValueString ()[0],
            result.getValueString ()[1],
            result.getValueString ()[2],
            result.getValueString ()[3],
            result.getValueString ()[4]
        };
    }

    // 以下，ファイル関係のメソッド．

    private BufferedReader makeBr (String filePath) { // BufferedReaderを作る．作れなければnullを返す．
        try {
            return new BufferedReader (new FileReader (filePath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace ();
            return null;
        }
    }

    private String[] readTsvLine (BufferedReader br) { // tsvファイルのBufferedReaderを1行読みこんで文字列配列にする．
        String line; // 行の文字列．
        
        try {
            line = br.readLine ();
        }
        catch (IOException e) {
            e.printStackTrace ();
            return null;
        }

        if (line == null) {
            return null;
        }
        else {
            return line.split ("\t");
        }
    }

    private BufferedWriter makeBw (String filePath) { // BufferedWriterを作る．作れなければnullを返す．
        try {
            File file = new File (filePath);
            if (file.exists () == false) { // ファイルが存在しない場合．
                boolean result = file.createNewFile ();
            }

            return new BufferedWriter (new FileWriter (filePath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace ();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace ();
            return null;
        }
    }

    private void writeTsvLine (BufferedWriter bw, String[] row) { // tsvファイルに行を書き込む．
        try {
            bw.write (String.join ("\t", row));
            bw.newLine ();
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private void closeBr (BufferedReader br) { // BufferedReaderを閉じる．
        try {
            br.close ();
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private void closeBw (BufferedWriter bw) { // BufferedWriterを閉じる．
        try {
            bw.close ();
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }
}