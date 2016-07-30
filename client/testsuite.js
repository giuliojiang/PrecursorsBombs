// Mockeries
// ----------------------------------------------------------------------------
function NetworkMockery()
{
    this.lastmsg = '';
    
    this.send = function(msg)
    {
        this.lastmsg = msg;
        return msg;
    }
}

function SystemServicesMockery()
{
    this.currentTime = 0;
    
    this.now = function()
    {
        return this.currentTime;
    }
}

// Testsuite manager
// ----------------------------------------------------------------------------
function TestsuiteManager()
{
    this.tests_run = 0;
    this.tests_passed = 0;
    
    this.exit = function()
    {
        return this.tests_run == this.tests_passed ? 0 : 1;
    }
}

var testsuite_manager = new TestsuiteManager();


// ----------------------------------------------------------------------------
// Testsuite utilities

var print_to_console = true;
var approx_threshold = 0.001;

function print(str)
{
    if (print_to_console)
    {
        console.log(str);
    } else
    {
        document.write('<p>' + str + '</p>');
    }
};

function equalsApprox(a, e)
{
    if (isNumeric(a) && isNumeric(e))
    {
        if (Math.abs(a - e) < approx_threshold)
        {
            return true;
        }
    }
    return false;
};

function assertTrue(t)
{
    testsuite_manager.tests_run++;
    if (!t)
    {
        print("Assertion failed");
    } else
    {
        testsuite_manager.tests_passed++;
    }
}

function assertEquals(a, e)
{
    testsuite_manager.tests_run++;
    if (!(a == e))
    {
        print("Expected=" + e + ";Actual=" + a);
    } else
    {
        testsuite_manager.tests_passed++;
    }
};

function assertEqualsStrict(a, e)
{
    testsuite_manager.tests_run++;
    if (!(a === e))
    {
        print("Expected=" + e + ";Actual=" + a);
    } else
    {
        testsuite_manager.tests_passed++;
    }
};

function assertEqualsFloat(a, e)
{
    testsuite_manager.tests_run++;
    if (!(equalsApprox(a,e)))
    {
        print("Expected=" + e + ";Actual=" + a);
    } else
    {
        testsuite_manager.tests_passed++;
    }
};

function assertContains(s, substring)
{
    testsuite_manager.tests_run++;
    if (!(s.indexOf(substring) > -1))
    {
        print("Expected to contain=" + substring + ";Actual=" + s);
    } else
    {
        testsuite_manager.tests_passed++;
    }
}

// Camera tests
function test_geometry_utils()
{
    print("test_geometry_utils");
    assertEqualsFloat(normalizeRadians(0), 0);
    assertEqualsFloat(normalizeRadians(2*pi), 0);
    assertEqualsFloat(normalizeRadians(-7.15), 5.416368);
    assertEqualsFloat(normalizeRadians(59.59), 3.041344);
    assertEqualsFloat(normalizeRadians(-109.30), 3.797312);
};

// Event tests
function test_player_position_updates(game_logic)
{
    print("test_player_position_updates");
    
    var FakePlayer = function()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        
        this.updatePosition = function(x,y,z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        this.updateAlpha = function(a)
        {
            
        }
    }
    
    var p = new FakePlayer();
    
    game_logic.addPlayer(p, 'null');
    
    var obj = 
    {
        'type' : 'player',
        'id' : 'null',
        'alpha' : 0.5694,
        'x' : 1,
        'y' : 2,
        'z' : 3
    };
    
    handleEvent(obj, game_logic);
    
    assertEqualsFloat(p.x, 1);
    assertEqualsFloat(p.y, 2);
    assertEqualsFloat(p.z, 3);
}

// GameLogic tests
function test_game_logic_holder()
{
    print('test_game_logic_holder');
    
    game_logic = new GameLogic(system);
    
    var p1 = {};
    var p2 = {};
    
    game_logic.addPlayer(p1, 'player1');
    game_logic.addPlayer(p2, 'player2');
    
    assertEqualsStrict(game_logic.getPlayer('player1'), p1);
    assertEqualsStrict(game_logic.getPlayer('player2'), p2);
    
    game_logic.removePlayer('player1');
    assertEqualsStrict(game_logic.getPlayer('player1'), undefined);
}

// CameraLogic tests
function tests_camera_logic()
{
    print('tests_camera_logic');
    
    system.currentTime = 100;
    
    var camera = new CameraLogic(system);
    
    msg = camera.updateRotation(1.5);
        
    assertContains(msg, 'type');
    assertContains(msg, 'player');
    assertContains(msg, 'rotation');
    assertContains(msg, '1.5');
}

// Bomb and explosion tests
function tests_explosion_logic(game_logic, system)
{
    print('tests_explosion_logic');
    
    system.currentTime = 10;
    game_logic.addExplosion(0,0,'|');
    var e = undefined;
    for (var key in game_logic.allExplosions)
    {
        
        e = game_logic.allExplosions[key];
    }
    assertTrue(!e.finished());
    system.currentTime = 500;
    assertTrue(!e.finished());
    system.currentTime = 700;
    assertTrue(e.finished());
}

// Testsuite globals

var socketURL = 'ws://localhost';

var network = new NetworkMockery();

var system = new SystemServicesMockery();

var game_logic = new GameLogic(system);

var game_controller = new GameController(game_logic);

// -----------------------------------------------------------------------------
// Testsuite start

print("Starting testsuite...");

test_geometry_utils();
test_player_position_updates(game_logic);
test_game_logic_holder();
tests_camera_logic();
tests_explosion_logic(game_logic, system);

print("Testsuite completed...");
print('Passed ' + testsuite_manager.tests_passed + '/' + testsuite_manager.tests_run + ' tests');
process.exit(testsuite_manager.exit())

// -----------------------------------------------------------------------------
// Testsuite end