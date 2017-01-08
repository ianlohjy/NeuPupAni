import time
import sys

def run(argv):
    # Eat some RAM
    # http://stackoverflow.com/questions/6317818/how-to-eat-memory-using-python

    eat_ram = ' ' * 512000000

    for arg in argv:
        if(arg.isdigit()):
            print("Sleeping for %s ..." % (arg))
            sys.stdout.flush()
            time.sleep(int(arg))
            print("Woken up after %s seconds!" % (arg))
            # According to https://www.turnkeylinux.org/blog/unix-buffering
            # unless python script is run from terminal, stdout is put into a 4096 byte buffer, 
            # so you need to flush out the buffer manually to get it displaying line by line
            sys.stdout.flush()
    print("Program finished!")

if __name__ == "__main__":
    run(sys.argv[1:])

    