package no.fractal.database;

import no.fractal.database.Models.TensorData;
import no.fractal.database.Models.User;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs CRUD operations against the postgres database.
 */
public class GateQueries extends PsqlDb {

    private static final Logger LOGGER = Logger.getLogger(GateQueries.class.getName());

    /**
     * Returns a TensorData list with all the tensor in the database.
     * If there are no results, an empty is returned.
     *
     * @return all tensors in database, list can be empty.
     * @throws SQLException thrown if problems with database
     */
    public static ArrayList<TensorData> getAllTensors() throws SQLException {
        String query = "SELECT user_id, face_vec FROM login_referance;";

        ArrayList<TensorData> tensorList = new ArrayList<>();

        sqlQuery(query, resultSet -> {
            try {
                var      vectors        = (BigDecimal[]) resultSet.getArray("face_vec").getArray();
                double[] vectorToDouble = new double[vectors.length];
                for (int i = 0; i < vectors.length; i++) {
                    vectorToDouble[i] = vectors[i].doubleValue();
                }
                tensorList.add(new TensorData(vectorToDouble, UUID.fromString(resultSet.getString("user_id"))));
            } catch (ClassCastException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        });

        return tensorList;
    }

    /**
     * Returns a timestamp for when the last change in the tensor data table occurred.
     *
     * @return timestamp for when last change of tensors occurred.
     * @throws SQLException thrown if problems with database
     */
    public static long getLastTensorTableUpdate() throws SQLException {
        String query = String.format("SELECT last_change FROM updates_table where id = '%s';", "new_user_queue");

        AtomicLong ret = new AtomicLong();
        sqlQuery(query, resultSet -> {
            ret.set(resultSet.getLong("last_change"));
        });

        return ret.get();

    }

    /**
     * Returns a user from the database with the provided id.
     *
     * @param userID userId of the user to get
     * @return returns a user or null if not found
     * @throws SQLException thrown if problems with database
     */
    public static User getUserByID(UUID userID) throws SQLException {
        String query = String.format(
                "SELECT users.firstname, users.lastname, login_referance.file_name FROM users INNER JOIN login_referance ON users.id=login_referance.user_id where id = '%s' ;",
                userID.toString());

        AtomicReference<User> ret = new AtomicReference<>();

        sqlQuery(query, resultSet -> {
            ret.set(new User(userID, resultSet.getString("firstname"), resultSet.getString("lastname"),
                             resultSet.getString("file_name")));
        });

        return ret.get();
    }

    /**
     * Inserts a new user entered event record in the database.
     * The event is registered to a user by its id and the station the user entered.
     *
     * @param user_id id of the user entering.
     * @param stationId id of the gate station the user entered on.
     * @throws SQLException thrown if problems with database
     */
    public static void createUserEnteredEvent(UUID user_id, UUID stationId) throws SQLException {
        String query = String.format("INSERT INTO user_enter_events (user_id, station_id)  VALUES ('%s', '%s');",
                                     user_id, stationId);

        sqlUpdate(query);
    }

    /**
     * Adds an unidentified user to the registration queue.
     * The user is added by a UUID, face features and the gate station the user was unidentified on.
     *
     * @param user_id the id of the user to create
     * @param tensorData the face features tensors of the user
     * @param stationId the id of the station the user was unidentified on
     * @throws SQLException thrown if problems with database
     */
    public static void addNewUserToRegistrationQueue(UUID user_id, TensorData tensorData, UUID stationId) throws SQLException {
        String query = String.format(
                "INSERT INTO new_user_queue (tmp_id, face_vec, station_id)  VALUES ('%s', %s, '%s');", user_id,
                tensorData.asSQLString(), stationId);

        sqlUpdate(query);
    }

    /**
     * Adds a thumbnail reference to a unidentified user in the registration queue.
     * The thumbnail is linked to the user by the user id used when adding the
     * unidentified user to the registration queue.
     *
     * @param user_id the id of the user in the registration queue.
     * @param thumbnail the thumbnail of the user
     * @throws SQLException thrown if problems with database
     */
    public static void addThumbnailToNewUserInRegistrationQueue(UUID user_id, File thumbnail) throws SQLException {
        String query = String.format("UPDATE new_user_queue SET file_name='%s' where tmp_id='%s';", thumbnail.getName(),
                                     user_id);

        sqlUpdate(query);
    }

    /**
     * Check the database of the UUID already exists in the registration queue.
     * Returns true if exists, else false.
     *
     * @param userId the id to check if exists in the registration queue.
     * @return true if id exists, else false
     * @throws SQLException thrown if problems with database
     */
    public static boolean isIdInRegistrationQueue(UUID userId) throws SQLException {
        String query = String.format("SELECT tmp_id FROM new_user_queue where tmp_id = '%s';", userId.toString());

        AtomicBoolean ret = new AtomicBoolean(false);

        sqlQuery(query, resultSet -> {
            ret.set(!resultSet.getString("tmp_id").isBlank());
        });

        return ret.get();

    }

    /**
     * Validates the gate station login secret with the database.
     * Returns true of it is valid, else false.
     * @param stationId the id of the station trying to validate.
     * @param stationSecret the secret of the station.
     * @return true of valid, else false.
     * @throws SQLException thrown if problems with database
     */
    public static boolean isStationLoginValid(UUID stationId, String stationSecret) throws SQLException {
        String query = String.format("SELECT login_key FROM stations where id = '%s';", stationId.toString());

        AtomicBoolean stationValid = new AtomicBoolean(false);

        sqlQuery(query, resultSet -> {
            stationValid.set(resultSet.getString("login_key").equals(stationSecret));
        });
        return stationValid.get();
    }

    /**
     * Remove all expired new user registrations.
     *
     * @param timeLimit unix time for when expiration is due.
     * @return returns the id and the thumbnail for the unregistered user.
     * @throws SQLException thrown if problems with database
     */
    public static HashMap<UUID, File> removeExpiredNewUserRegistrations(long timeLimit) throws SQLException {

        String query = String.format("DELETE FROM new_user_queue WHERE added_ts > %d RETURNING tmp_id, file_name;",
                                     timeLimit);

        HashMap<UUID, File> result = new HashMap<>();

        sqlQuery(query, resultSet -> {
            UUID uuid = resultSet.getString("tmp_id") != null ? UUID.fromString(resultSet.getString("tmp_id")) : null;
            File file = resultSet.getString("file_name") != null ? new File("file_name") : null;
            result.put(uuid, file);
        });

        return result;

    }

    /**
     * Seeds the database with a given amount of tensors.
     *
     * @param numberOfTensors how many tensors to insert into the database
     * @throws SQLException thrown if problems with database
     */
    public static void SeedDBWithTensors(int numberOfTensors) throws SQLException {
        Connection connection = tryConnectToDB();
        Statement  statement  = connection.createStatement();
        var        i          = 0;
        for (i = 0; i < numberOfTensors; i++) {
            String query = "INSERT INTO login_referance (user_id, face_vec, file_name) VALUES ('00000000-0000-0000-0000-000000000000','{0.018430151045322418, 0.04757530987262726, 0.010215401649475098, 0.008493672125041485, 0.08091679215431213, 0.005828132852911949, 0.026049500331282616, -0.011415423825383186, -0.03573146462440491, 0.009119799360632896, 0.02516913041472435, 0.06656073778867722, -0.0790158212184906, 0.002019329695031047, 0.0031783301383256912, 0.04704738035798073, 0.0537136010825634, 0.006321453023701906, -0.01492258720099926, 0.0039352900348603725, 0.02565939910709858, -0.021060079336166382, -0.023172877728939056, 0.05936763435602188, 0.07285358756780624, -0.08550016582012177, 0.054503172636032104, 0.016863537952303886, 0.009892970323562622, 0.008351816795766354, -0.034024130553007126, -0.08304302394390106, -0.005895460955798626, 0.05894750729203224, -0.016724636778235435, 0.03842145949602127, -0.0013848048401996493, -0.03175966069102287, 0.020520424470305443, 0.002457728609442711, -0.0009009691420942545, -0.017699120566248894, 0.0036447534803301096, -0.016407743096351624, 0.019741075113415718, 0.07486727833747864, -0.01888534240424633, -0.05951633304357529, 0.055024899542331696, 0.07073068618774414, 0.10584945976734161, -0.0696503296494484, 0.006537090986967087, 0.0570744089782238, 0.024949941784143448, -0.04701459780335426, -0.07823147624731064, 0.0547049455344677, -0.010088407434523106, -0.0014591715298593044, 0.019011683762073517, -0.011797282844781876, 0.016088632866740227, 0.027528680860996246, 0.007008429151028395, 0.006295477505773306, -0.014452784322202206, 0.037405338138341904, 0.08511146903038025, 0.027260441333055496, -0.015473749488592148, -0.02090238407254219, 0.017198337242007256, 0.04689298942685127, 0.033304933458566666, 0.05503019317984581, 0.026045022532343864, 0.052769482135772705, 0.04196851700544357, 0.09746642410755157, 0.042769771069288254, 0.02814415469765663, -0.01592779904603958, -0.03917419910430908, 0.06562645733356476, 0.027388768270611763, 0.0158962681889534, 0.007721430622041225, 0.07110046595335007, 0.06938796490430832, -0.05697619915008545, 0.054864902049303055, -0.021023614332079887, 0.041197098791599274, -0.05693390592932701, -0.06154623255133629, -0.02225165255367756, 0.09798754006624222, 0.003731656586751342, -0.01822519488632679, -0.01666363887488842, 0.05315601825714111, -0.0202254019677639, -0.06015210598707199, 0.06966147571802139, 0.006814229302108288, 0.040181636810302734, -0.008114403113722801, 0.04432547464966774, -0.04413336515426636, -0.016597265377640724, -0.06591392308473587, 0.03293260931968689, -0.014900734648108482, 0.07041116058826447, 0.017765691503882408, 0.035953864455223083, -0.07205814123153687, -0.028807956725358963, -0.021510088816285133, 0.005090867169201374, 0.08537578582763672, -0.0011518805986270308, -0.025911206379532814, 0.025704046711325645, -0.04063134640455246, 0.004290748853236437, -0.059980470687150955, 0.019393617287278175, 0.059017378836870193, -0.014710475690662861, 0.05609296262264252, -0.013417240232229233, -0.03542137145996094, -0.024967920035123825, -0.07763165235519409, 0.02778705209493637, 0.013375495560467243, -0.0375339649617672, -0.016167517751455307, 0.10798519849777222, 0.002627760637551546, -0.005691518075764179, 0.03615083172917366, -0.04880049452185631, -0.042398545891046524, -0.014114851132035255, 0.03018428571522236, 0.02832818031311035, -0.055458806455135345, -0.043020863085985184, -0.06409046798944473, -0.04609067738056183, 0.06894556432962418, -0.022544970735907555, -0.04404905438423157, -0.03149140626192093, 0.014721894636750221, -0.057089291512966156, 0.02245771326124668, 0.03713295981287956, 0.02592177875339985, 0.008962124586105347, 0.00282336981035769, 0.04046519845724106, 0.0038921155501157045, -0.03477098420262337, 0.047929797321558, 0.006855377461761236, 0.03537715598940849, -0.07287067919969559, -0.04942223057150841, -0.06389962881803513, 0.007856634445488453, 0.040286485105752945, 0.04659833014011383, 0.03252911940217018, -0.008973650634288788, -0.011699759401381016, 0.00870179757475853, -0.00037100203917361796, -0.046155866235494614, -0.00027640137705020607, 0.015249277465045452, 0.0009233374730683863, -0.08726086467504501, 0.01989722065627575, -0.04437516629695892, -0.016849637031555176, 0.12199991196393967, -0.039809275418519974, 0.03368992358446121, 0.03319098427891731, -0.02127266861498356, 0.03458824381232262, -0.03258984908461571, 0.012467890977859497, -0.004596509505063295, 0.05580759048461914, -0.00794475618749857, 0.04671579599380493, -0.06162388250231743, 0.08328058570623398, 0.03372263163328171, 0.07802097499370575, -0.009816265664994717, 0.014074035920202732, 0.05062223598361015, -0.0030751563608646393, 0.04716883599758148, -0.05735519528388977, -0.03686169907450676, -0.016354907304048538, -0.06744802743196487, -0.04832151159644127, -0.030252104625105858, -0.03852539137005806, 0.0549486018717289, -0.06034022197127342, 0.020387552678585052, -0.09931348264217377, 0.012933126650750637, 0.08132772892713547, 0.011991017498075962, 0.031403008848428726, 0.0009036191622726619, -0.001657291199080646, 0.08971868455410004, -1.9557572159101255e-05, -0.006853419356048107, -0.006804839242249727, -0.07246312499046326, -0.019209716469049454, -0.015657393261790276, 0.07833628356456757, -0.03675530478358269, -0.0454232357442379, -0.022535908967256546, -0.04822010174393654, 0.10750044137239456, 0.013326732441782951, 0.003670343430712819, 0.05515075847506523, 0.09115328639745712, 0.10428614169359207, 0.05241918936371803, -0.05881500244140625, -0.04966411367058754, 0.016359563916921616, -0.04169802367687225, 0.030541587620973587, 0.1304975152015686, 0.016476057469844818, -0.022269969806075096, -0.0403895266354084, -0.07891938835382462, -0.040894005447626114, 0.0874907374382019, -0.01811983995139599, 0.011235151439905167, -0.03253990411758423, 0.0033133486285805702, -0.03926606476306915, -0.03760787472128868, -0.013658824376761913, 0.02099359594285488, 0.0390620194375515, 0.036796994507312775, -0.008085640147328377, 0.014466684311628342, -0.06699804961681366, -0.06324122846126556, -0.029472937807440758, -0.0014958513202145696, 0.0017151361098513007, 0.00920903030782938, -0.03015504591166973, -0.014621365815401077, -0.026430495083332062, -0.040366340428590775, -0.0576031468808651, 0.050764214247465134, -0.05103740468621254, -0.00992718618363142, 0.052205201238393784, -0.03428109735250473, -0.00601540831848979, 0.03663403168320656, -0.017828594893217087, -0.022639406844973564, -0.05634931102395058, -0.019868066534399986, 0.0017075929790735245, -0.07572533935308456, 0.056445326656103134, 0.034701865166425705, 0.011863311752676964, 0.08633339405059814, -0.020644186064600945, -0.042111583054065704, 0.10490774363279343, 0.02642621286213398, -0.04735465347766876, 0.06593754142522812, 0.018714549019932747, -0.019219232723116875, -0.014399142935872078, -0.03907708451151848, 0.02129698544740677, -0.04229176789522171, 0.04364054650068283, 0.04238022863864899, 0.06389566510915756, 0.07282547652721405, 0.112381212413311, -0.053915850818157196, 0.0022996151819825172, 0.032109200954437256, -0.0026393712032586336, -0.011493083089590073, 0.039413146674633026, -0.016528882086277008, -0.00637183990329504, 0.018263686448335648, -0.0040727779269218445, 0.033546093851327896, 0.010054182261228561, 0.015080810524523258, -0.005072914529591799, -0.005906667560338974, -0.01866878941655159, 0.0013578747166320682, -0.03430065140128136, 0.04381485655903816, 0.04105984419584274, 0.007219071500003338, 0.01227397657930851, -0.03292195126414299, 0.006204020231962204, 0.06379666924476624, 0.05202507600188255, 0.043776948004961014, -0.038207046687603, -0.023323914036154747, 0.03870342671871185, 0.016658013686537743, -0.02416854538023472, 0.03127095475792885, -0.0018125370843335986, -0.05090254917740822, 0.08111867308616638, 0.009585048072040081, -0.015020878985524178, 0.01247931644320488, -0.028602877631783485, 0.10701870918273926, 0.007197530474513769, 0.014366540126502514, -0.04511134326457977, -0.008266549557447433, -0.019180767238140106, -0.06737806648015976, 0.03608689457178116, 0.03501257672905922, -0.005362228024750948, -0.01894993707537651, 0.006398685742169619, -0.03489973396062851, 0.007465227972716093, 0.015600476413965225, -0.08828536421060562, -0.04301594942808151, 0.02939307689666748, -0.020712634548544884, 0.06374657154083252, 0.08854492008686066, -0.02382451668381691, -0.031199630349874496, -0.05122990161180496, 0.07979050278663635, 0.03653017431497574, 0.0398937463760376, -0.07999499142169952, 0.043472759425640106, 0.03347587212920189, 0.00917417649179697, -0.03511207178235054, 0.10367700457572937, 0.0021481604781001806, 0.03255334123969078, -0.09605304896831512, 0.028033407405018806, -0.046296145766973495, -0.006669311318546534, -0.06263694167137146, -0.0010342635214328766, 0.03344067931175232, -0.043393395841121674, 0.03743555396795273, 0.051605019718408585, -0.013344778679311275, -0.018721362575888634, 0.1015416830778122, -0.14455054700374603, -0.014304835349321365, -0.021689407527446747, 0.049945008009672165, -0.0477963425219059, 0.0796961560845375, 0.0033012402709573507, 0.07796341925859451, -0.03858911991119385, -0.009458126500248909, -0.0011378851486369967, -0.07864461094141006, -0.04919499531388283, -0.028115466237068176, 0.03173113986849785, -0.0004431676061358303, -0.02238347753882408, 0.02070339396595955, 0.026808224618434906, -0.02846929058432579, -0.0032493658363819122, -0.004092377610504627, 0.03366376459598541, -0.05616382136940956, 0.002854384481906891, 0.024346299469470978, -0.06366858631372452, 0.01811322569847107, 0.038447558879852295, -0.032608795911073685, -0.03146648034453392, -0.019160866737365723, -0.02604932337999344, -0.0442286841571331, -0.029292628169059753, -0.004893126897513866, -0.01611938513815403, -0.04631389304995537, -0.04029475525021553, 6.724156264681369e-05, 0.010949901305139065, -0.08056634664535522, 0.030081981793045998, -0.007086016703397036, 0.0689409151673317, 0.05213150009512901, -0.03628017380833626, 0.08042432367801666, -0.09116620570421219, -0.010331499390304089, -0.011135769076645374, -0.06828131526708603, 0.009876073338091373, 0.028399987146258354, 0.0377221517264843, -0.003538610180839896, 0.025460073724389076, -0.01462175790220499, -0.06189761683344841, 0.03632942959666252, 0.016972403973340988, -0.01440383866429329, -0.028546780347824097, -0.024377621710300446, -0.04722485691308975, -0.013685083948075771, -0.01835894025862217, -0.0369025319814682, -0.018856490030884743, -0.015494024381041527, -0.07977548986673355, -0.1132049411535263, 0.007986355572938919, 0.05406952649354935, 0.027679944410920143, -0.0030893865041434765, -0.06943737715482712, 0.05385173484683037, 0.0523109994828701, -0.0675632506608963, -0.028997350484132767, 0.005830689799040556, -0.03801468014717102, 0.014077036641538143, 0.012702448293566704, 0.032558586448431015, 0.007852942682802677, -0.02778136543929577, -0.019221864640712738, -0.0014240570599213243, 0.06357929110527039, 0.012554940767586231, 0.004706207197159529, -0.028476431965827942, 0.05607236549258232, 0.00311486073769629, 0.03659636154770851, 0.017107635736465454, 0.05440569669008255, 0.002950976835563779, -0.038164325058460236, 0.04685264453291893, -0.029657119885087013, 0.0589609295129776, 0.047238364815711975, 0.00389011949300766, -0.05083891749382019, 0.0708608329296112, -0.0234039556235075}', '00000000-0000-0000-0000-000000000000.jpg')";
            try {
                statement.addBatch(query);
                statement.executeQuery(query);
            } catch (SQLException throwables) {
            }
        }
        statement.close();
        connection.close();
    }

}
